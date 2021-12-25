const Attribute = {
    data() {
        return {
            user: null,
            username: null,

            searchInput: null,
            searchRes: null,

            dictID:1,

            status: {},

            words: null,

            pages: [],

            curHead: null,

        }
    },
    methods: {
        // 搜索
        doSearch() {
            let isletter = /^[a-zA-Z]+$/
            let that = this
            this.searchRes = null
            if (this.searchInput === null || this.searchInput.trim() == '')
                return
            if (isletter.test(this.searchInput)) {
                // console.log('query/en')
                $.post("/query/en", {
                    word: that.searchInput
                }, function (res) {
                    // console.log(res)
                    that.searchRes = JSON.parse(res.data)
                    // console.log(that.searchRes)
                    $('#myModal').modal()
                })
            } else {
                $.post("/query/zh", {
                    mean: that.searchInput, limit: 5
                }, function (res) {
                    // console.log(res)
                    that.searchRes = JSON.parse(res.data)
                    // console.log(that.searchRes)
                    $('#myModal').modal()
                })
            }
        },

        logout() {
            $.get("api/logout", function (res) {
                if (res == true) {
                    localStorage.removeItem("user")
                    $(location).attr('href', '/')
                }
            })
        },

        Confirm(userid,dictID) {
            let that = this
            console.log(userid, dictID)
            $.post("/collect/getCollect", {
                // userid: that.user.id,
                // dictID: that.status.dictID
                userid: userid,
                dictID: dictID
            }, function (result) {
                that.words = []
                for (let i = 0; i < result.data.length; i ++) {
                    try {
                        let word = JSON.parse(result.data[i])
                        let tran = []
                        for (info of word.trans) {
                            tran.push( info.pos + '. ' + info.tranCn + ' ' + '(' + info.tranOther + ')' )
                        }
                        word.trans = tran
                        that.words.push(word)
                    } catch (e) {
                        console.log(e)
                    }
                }

                that.loadPage(0)
            })
        },

        loadPage(isNext) {
            if (isNext == 0) {
                if (this.words.length > 0) {
                    this.curHead = 0
                    this.pages = this.words.slice(this.curHead, 5)
                }
            } else {
                let head = this.curHead + (isNext * 5)
                if (head < 0) {
                    return
                }
                if (this.words.length - head > 0) {
                    this.curHead = head
                    // console.log(this.curHead, this.words.slice(this.curHead,this.curHead + 5))
                    this.pages = this.words.slice(this.curHead,this.curHead + 5)
                }
            }
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

        getStatus() {
            let that = this
            $.post("/collect/getStatus", {
                userid: that.user.id
            }, function (res) {
                if (res.code == 200) {
                    let status = res.data
                    that.status = status
                    console.log(status.dictID)
                    that.Confirm(that.user.id,status.dictID)
                }
            })
        }
    },
    computed: {
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

            if (this.words.length == 0)
                return

            let word = this.words[this.curIndex]
            let prefix = 'https://dict.youdao.com/dictvoice?word='
            let uk = prefix + word.ukspeech
            let us = prefix + word.usspeech

            return {
                uk: uk,
                us: us
            }
        }
    },

    created() {
        let jsonStr = localStorage.getItem("user")
        this.user = JSON.parse(jsonStr)
        // this.user = { id: 16, mail: "877669110@qq.com", phone: "13553285743", nickName: "moonlight" }
        this.username = this.user.nickName
        // this.Confirm()

        this.getStatus()
    },
}
Vue.createApp(Attribute).mount('#app')


let handler = setInterval(function () {
    $.get("api/isLogin", function (res) {
        if (res.data !== true) {
            clearInterval(handler)
            alert("您的账号已经在别处登录！")
            $(location).attr("href", "/")
        }
    })
}, 3000)

