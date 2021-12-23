const AttributeBinding = {
    data() {
        return {
            user: null,
            words: [],
            curIndex: 0,

            searchInput: null,
            searchRes: null,

            isPlay: true,
            timer: '',

            delay: 5000,

            status,
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
        setDelay(num) {
            this.delay = num * 1000
        },
        startSildes(isPlaying) {
            let that = this
            // console.log(isPlaying);
            if (isPlaying) {
                this.isPlay = false
                clearInterval(this.timer)
                // console.log('clear');
                $('#btnDelay').attr("disabled",false)
            } else {
                this.timer = setInterval(function () {
                    that.curIndex ++;
                    if (that.curIndex == 10) {
                        that.words = []
                        that.curIndex = 0
                        that.getWords(that.user.id, 10)
                    }
                }, that.delay)
                this.isPlay = true
                // console.log('start');
                $('#btnDelay').attr("disabled",true)
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

        getWords(userid,  count) {
            let that = this
            // console.log(userid, count)
            $.post("/home/getSlides", {
                userid: userid,
                count: count
            }, function (res) {
                // console.log(res)
                if (res.code == 200) {
                    for (let i = 0; i < res.data.length; i ++) {
                        let word = JSON.parse(res.data[i])
                        // console.log(word)
                        that.words.push(word)
                    }
                    // console.log(that.words);
                    if (that.timer == '')
                        that.startSildes(false);
                }
            })
        },

        load() {
            this.getWords(this.user.id, 10);
        }
    },
    computed: {
        curWord() {
            if (this.words.length == 0) {
                return {word: '', remMethod: {val: ''}, sentences: [], comm: []}
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

        // this.user = {id:16, mail:"877669110@qq.com", phone:"13553285743", nickName:"moonlight"}

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