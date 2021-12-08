const Attribute = {
    data() {
        return {
            user: null,

            curSelectedCnt: 20,
            curSelectedBook: 1,

            books: [],

            searchInput: null,
            searchRes: null,

            curPlan: '',
            cnts: [5, 10, 15, 20, 25, 30, 50]
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
                console.log('query/en')
                $.post("/query/en", {
                    word: that.searchInput
                }, function (res) {
                    console.log(res)
                    that.searchRes = JSON.parse(res.data)
                    console.log(that.searchRes)
                    $('#myModal').modal()
                })
            } else {
                $.post("/query/zh", {
                    mean: that.searchInput, limit: 5
                }, function (res) {
                    console.log(res)
                    that.searchRes = JSON.parse(res.data)
                    console.log(that.searchRes)
                    $('#myModal').modal()
                })
            }
        },

        open(dictID) {
            this.curSelectedBook = dictID
            console.log(this.curSelectedBook)

            // $('#myModal1').modal()
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

        updateBook() {
            let that = this
            $.post("/book/updateBook", {
                userid: that.user.id,
                dictID: that.curSelectedBook,
                studyWord: that.curSelectedCnt
            }, function (res) {
                console.log(res)
                if (res.code == 200) {
                    bs4pop.notice( res.data, {position: 'topcenter', type: 'success'} )
                    $('#myModal1').modal('hide')
                    that.getCurPlan()
                } else {
                    bs4pop.notice( res.msg, {position: 'topcenter', type: 'warning'} )
                }
            })
        },

        logout() {
            $.get("api/logout", function (res) {
                if (res == true) {
                    localStorage.removeItem("user")
                    $(location).attr('href', '/')
                }
            })
        },

        getCurPlan() {
            let that = this
            $.post("/book/getPlans", {
                userid: that.user.id
            }, function (res) {
                if (res.code == 200) {
                    that.curPlan = res.data
                }
            })
        },

        getStatus() {
            let that = this
            $.post("/book/getBooks", {

            }, function (res) {
                if (res.code == 200) {
                    that.books = res.data
                    for (let i = 0; i < that.books.length; i ++) {
                        that.books[i].imgUrl = '/images/book/book' + that.books[i].dictID + '.png'
                    }
                    console.log(that.books)
                } else {
                    bs4pop.notice( res.msg, {position: 'topcenter', type: 'warning'} )
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
        // this.username = this.user.nickName
        this.getStatus()
        this.getCurPlan()
    },
}
Vue.createApp(Attribute).mount('#app')