const AttributeBinding = {
    data() {
        return {
            user: null,

            searchInput: null,
            searchRes: null,

            dictId: 1,
            words: [],
            curIndex: 0,

            is_lreaning: false,
            is_detail: false,
            is_test: false,
            is_home: true,
            is_done: false,
            is_null: false,

            status,
            // todayNum: 3,
            // hadNum: 0,
            // totalNum: 3550,

            isConfirm: false,
            is_recognize: false,
            speelWord: '',
            is_invaild: false,
            testInfo: '请输入单词的拼写'
        }
    },
    methods: {
        toLearn() {
            $(location).attr('href', '/main')
        },
        logout() {
            // $.post()
            $.get("api/logout", function (res) {
                if (res == true) {
                    localStorage.removeItem("user")
                    $(location).attr('href', '/')
                }
            })
        },
        // 搜索
        doSearch() {
            let isletter = /^[a-zA-Z]+$/
            let that = this
            this.searchRes = null
            if (this.searchInput === null || this.searchInput.trim() == '')
                return
            if (isletter.test(this.searchInput)) {
                // // console.log('query/en')
                $.post("/query/en",{
                    word: that.searchInput
                }, function (res) {
                    // console.log(res)
                    that.searchRes = JSON.parse(res.data)
                    // console.log(that.searchRes)
                    $('#myModal').modal()
                })
            } else {
                $.post("/query/zh",{
                    mean: that.searchInput, limit: 5
                }, function (res) {
                    // // console.log(res)
                    that.searchRes = JSON.parse(res.data)
                    // // console.log(that.searchRes)
                    $('#myModal').modal()
                })
            }
        },
        // 开始学习按钮
        starLreaning() {
            this.is_home = false
            this.is_lreaning = true
            $('#button-addon2').attr("disabled",true)
        },
        // 认识 or 不认识
        recognize(is_recognize) {
            this.is_lreaning = false
            this.is_detail = true
            this.is_recognize = is_recognize
            $('#button-addon2').attr("disabled",false)
        },
        // 已学习
        next() {
            this.is_detail = false
            if (this.is_recognize) {
                if (this.curIndex + 1 >= this.words.length) {
                    this.curIndex = 0
                    this.is_test = true
                    $('#button-addon2').attr("disabled",true)
                    return
                }
                this.curIndex ++
            }
            this.is_lreaning = true
            $('#button-addon2').attr("disabled",true)
        },
        // 回到首页
        backHome() {
            $(location).attr('href', '/main')
        },
        // 拼写确认
        testConfirm() {
            let that = this
            if (this.speelWord.trim() == '') {
                // this.is_invaild = true
                bs4pop.notice('请输入单词拼写', {position: 'topcenter', type: 'primary'})
                // setTimeout(function () {
                //     that.is_invaild = false
                //     that.testInfo = '请输入单词的拼写'
                // }, 1000)
                return
            }
            this.isConfirm = true
            if (that.speelWord.trim() === that.words[that.curIndex].json.word.trim()) {
                bs4pop.notice('拼写正确', {position: 'topcenter', type: 'success'})
            } else {
                bs4pop.notice('拼写错误', {position: 'topcenter', type: 'warning'})
            }

            setTimeout(function () {
                that.isConfirm = false
                // // console.log(that.words[that.curIndex].json.word.trim())
                if (that.speelWord.trim() === that.words[that.curIndex].json.word.trim()) {
                    // console.log('正确')

                    // // console.log(that.curIndex);
                    $.post("/Review/updatePriorWord", {
                        userid: that.user.id,
                        dictID: that.words[that.curIndex].dictID,
                        id: that.words[that.curIndex].json.wordRank,
                        isRight: true
                    })
                    if (that.curIndex + 1 >= that.words.length) {
                        // // console.log(that.curIndex, "++");
                        that.is_test = false
                        that.is_done = true
                        $.post("/Review/setReviewStatus", {
                            userid: that.user.id
                        })
                        that.curIndex = 0
                        $('#button-addon2').attr("disabled",false)
                        return
                    }
                    that.curIndex ++

                } else {
                    // console.log('拼写错误')
                    // // console.log(that.curIndex);
                    $.post("/Review/updatePriorWord", {
                        userid: that.user.id,
                        dictID: that.words[that.curIndex].dictID,
                        id: that.words[that.curIndex].json.wordRank,
                        isRight: false
                    })
                    if (that.curIndex + 1 >= that.words.length) {
                        // // console.log(that.curIndex, "++");
                        that.is_test = false
                        that.is_done = true
                        $.post("/Review/setReviewStatus", {
                            userid: that.user.id
                        })
                        $('#button-addon2').attr("disabled",false)
                        that.curIndex = 0
                        return
                    }
                    that.curIndex ++
                }
                that.speelWord = ''
            }, 2000)
        },
        playAudio(isUK) {
            let wordAudio = $('#wordAudio')[0]
            if (isUK == 1) {
                wordAudio.src = this.curSpeech.uk
                wordAudio.load()
                wordAudio.play()
            } else if(isUK == 2) {
                wordAudio.src = this.curSpeech.us
                wordAudio.load()
                wordAudio.play()
            } else if(isUK == 3) {
                wordAudio.src = 'https://dict.youdao.com/dictvoice?word=' + this.searchRes.word + '&type=1'
                wordAudio.load()
                wordAudio.play()
            }
        },

        getWords(userid,  count, isMore) {
            let that = this
            // console.log(userid, count, isMore)
            $.post("/Review/getWords", {
                userid: userid,
                count: count,
                isMore: isMore
            }, function (res) {
                // // console.log(res)
                if (res.code == 200) {
                    if (res.data.length == 0) {
                        that.is_home = false
                        that.is_null = true
                    }
                    for (let i = 0; i < res.data.length; i ++) {
                        let word = JSON.parse(res.data[i].json)
                        res.data[i].json = word
                        // // console.log(word)
                        that.words.push(res.data[i])
                    }
                    // // console.log(that.words);
                }
            })
        },

        async load() {
            let that = this
            $.post("/Review/getStatus", {
                userid: that.user.id
            }, function (res) {
                if (res.code == 200) {
                    // // console.log(res.data)
                    let status = res.data
                    if (status.isNotPlan || status.isNotMen) {
                        that.is_home = false
                        that.is_null = true
                        return
                    }
                    that.status = status

                    let isMore = false
                    if (status.isDone) {
                        isMore = true
                    }

                    if (status.isNotPlan)
                        return

                    that.getWords(that.user.id, status.count, isMore)
                }
            })
        }
    },
    computed: {
        curWord() {
            if (this.words === null) {
                return ''
            }
            return this.words[this.curIndex]
        },
        searchWordExchange() {
            let arr = this.searchRes.exchange.split('/')
            let res = ''
            for (let i = 0; i < arr.length; i ++) {
                arr[i] = arr[i].replace(/p:/, '过去式：')
                arr[i] = arr[i].replace(/d:/, '过去分词：')
                arr[i] = arr[i].replace(/i:/, '现在分词：')
                arr[i] = arr[i].replace(/3:/, '第三人称单数：')
                arr[i] = arr[i].replace(/r:/, '形容词比较级：')
                arr[i] = arr[i].replace(/t:/, '形容词最高级：')
                arr[i] = arr[i].replace(/s:/, '名词复数形式：')
                res += arr[i]
            }
            return res
        },
        curSpeech() {
            let word = this.words[this.curIndex]
            let prefix = 'https://dict.youdao.com/dictvoice?word='
            let uk = prefix + word.json.ukspeech
            let us = prefix + word.json.usspeech

            return {
                uk: uk,
                us: us
            }
        }
    },

    created() {

        let jsonStr = localStorage.getItem("user")
        this.user = JSON.parse(jsonStr)

        // this.user = {id:17, mail:"877669110@qq.com", phone:"13553285743", nickName:"moonlight"}

        this.load()
    },
}

Vue.createApp(AttributeBinding).mount('#app')


let handler = setInterval(function () {
    $.get("api/isLogin", function (res) {
        if (res.data !== true) {
            clearInterval(handler)
            alert("您的账号已经在别处登录！")
            $(location).attr("href", "/")
        }
    })
}, 3000)