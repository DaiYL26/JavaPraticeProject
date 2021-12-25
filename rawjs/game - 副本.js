        let time = 60
        if (!window.WebSocket) {
            alert('您的浏览器不支持WebSocket！，无法使用该功能')
        }

        const AttributeBinding = {
            data() {
                return {

                    words: [],
                    curIndex: 0,

                    userid: '',

                    is_test: false,
                    is_matching: false,
                    is_ready: false,
                    is_commit: false,
                    is_begin: true,

                    opponent: {},
                    selfScore: 0,

                    selfRank: 0,
                    
                    isConfirm: false,
                    is_recognize: false,
                    speelWord: '',
                    is_invaild: false,
                    testInfo: '请输入单词的拼写'
                }
            },
            methods: {

                next(cnt) {
                    if (this.curIndex + cnt >= 0 && this.curIndex + cnt < this.words.length) {
                        this.curIndex += cnt
                    }
                },

                loadWords() {
                    // // console.log(this.gameId);
                    let that = this
                    $.post("/game/getGameWords", {
                        gameId: that.gameId
                    }, function (res) {
                        // // console.log(res);
                        if (res.code == 200) {
                            let words = JSON.parse(res.data)
                            // // console.log(res.data);
                            
                            for (let i = 0; i < words.length; i ++) {
                                try {
                                    let word = JSON.parse(words[i].json)
                                    that.words.push(word)
                                } catch (error) {
                                    // // console.log(error.message);
                                }
                            }

                            // // console.log(that.words);

                            setTimeout(function (params) {
                                that.send('', 'READY')
                            }, 1000)
                        } else if (res.code == 500) {
                            alert(res.msg)
                        }
                    })
                },

                onMatchSuccess(FrameData) {
                    let data = FrameData.split(";")
                    this.gameId = data[1]
                    this.is_matching = false
                    this.is_ready = true

                    let rival = JSON.parse(data[0])
                    // // console.log(rival);
                    this.opponent.id = rival.id
                    this.opponent.name = rival.name
                    this.opponent.score = 0
                    this.opponent.rankScore = rival.score

                    this.loadWords()
                },

                init() {
                    let that = this

                    // open connection
                    this.socket.onopen = function(event) {
                        that.send('', 'START_MATCHING')
                    }

                    // on some worng happen
                    this.socket.onerror = function(event) {
                        alert('连接超时')
                        that.cancelMatching()
                        return
                    }

                    // on recevie message from the server
                    this.socket.onmessage = function (event) {
                        // // console.log(event);
                        // // console.log(event.data);
                        let socketFrame = JSON.parse(event.data)

                        if (socketFrame.type === 'MATCH_SUCCESS') {
                            that.opponentId = socketFrame.id
                            that.opponentName = socketFrame.name
                            that.onMatchSuccess(socketFrame.data)

                        } else if (socketFrame.type === 'START') {

                            that.is_ready = false
                            that.is_test = true
                            
                            that.timer = setInterval(function () {
                                if (time > 0) {
                                    let content = '对战剩余 ' + (--time) + ' 秒'
                                    $('#timeHodler').text(content)
                                }
                            }, 1000)
                            
                        } else if (socketFrame.type === 'COMMIT') {
                            let data = socketFrame.data.split(";")
                            // // console.log(data);

                            that.is_test = false
                            that.is_commit = true

                            clearInterval(that.timer)
                        } else if (socketFrame.type === 'CORRECT') {
                            if (socketFrame.data === 'true') {
                                // that.curIndex ++
                                that.words.splice(that.curIndex, 1)
                                that.selfScore ++
                                bs4pop.notice('正确', {position: 'topcenter', type: 'success'})
                                // that.errorCnt = 0
                            } else if (socketFrame.data === 'false') {
                                that.opponent.score ++
                            }
                            // // console.log(socketFrame.data);
                        } else if (socketFrame.type === 'WRONG') {
                            bs4pop.notice('拼写错误', {position: 'topcenter', type: 'warning'})
                            // that.errorCnt ++
                            // if (that.errorCnt == 3) {
                            //     that.errorCnt = 0
                            //     bs4pop.notice(that.curWord.word, {position: 'topcenter', type: 'primary'})
                            //     that.curIndex ++
                            // }
                            // // console.log(socketFrame.data);
                        } else if (socketFrame.type === 'CANCEL_MATCHING') {
							that.socket.close()
							that.socket = null
							that.is_matching = false
							that.is_begin = true
							if (socketFrame.data !== '') {
								alert(socketFrame.data)
							}
						}

                    }
                },

                send(message, type) {
                    let that = this
                    if (!this.socket) {
                        return;
                    }
                    if (this.socket.readyState == WebSocket.OPEN) {
                        let obj = {}

                        obj.id = parseInt(this.user.id)
                        obj.type = type
                        if (type === 'START_MATCHING') {
                            obj.data = $.cookie('satoken')
                        } else if (type === 'ANSWER') {
                            let answer = {}
                            answer.spelling = message
                            answer.wordRank = that.curWord.wordRank
                            answer.gameId = that.gameId
                            obj.data = JSON.stringify(answer)
                        }

                        // // console.log(JSON.stringify(obj));
                        this.socket.send(JSON.stringify(obj))
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

                toGame() {
                    $(location).attr('href', '/game')
                },
                // 开始学习按钮
                startMatching() {
                    this.is_begin = false
                    this.is_matching = true
                    this.socket = new WebSocket('ws://120.77.222.189:8801/game')

                    this.init()
                },

                cancelMatching() {
                    
                    if (this.socket) {
                        this.send('', 'CANCEL_MATCHING')
                        this.socket.close()
                        this.socket = null
                    }

                    this.is_matching = false
                    this.is_begin = true
                },

                // 拼写确认
                testConfirm() {
                    if (!this.speelWord.trim()) {
                        bs4pop.notice('请输入单词', {position: 'topcenter', type: 'warning'})
                        return
                    }

                    let answer = this.speelWord.trim()

                    this.send(answer, "ANSWER")
                    this.speelWord = ''
                    
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

                beforeunloadHandler (e) {
                    if (this.is_matching) {
                        this.cancelMatching()
                        e = e || window.event
                    
                        if (e) {
                            e.returnValue = '关闭提示'
                        }
                        // debugger
                        return '关闭提示'
                    }

                },

                load() {
                    let that = this
                    $.post("/game/getStatus", {
                        id: that.user.id
                    }, function (res) {
                        if (res.code == 200) {
                            that.selfRank = res.data
                            // // console.log(23);
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

                curSpeech() {
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
                window.addEventListener('beforeunload', e => this.beforeunloadHandler(e))
                let jsonStr = localStorage.getItem("user")
                // let jsonStr = '{"id":16,"mail":"877669110@qq.com","phone":"13553285743","nickName":"moon"}'
                this.user = JSON.parse(jsonStr)
                this.load()
            },
            
            destroyed() {
                window.removeEventListener("beforeunload", (e) =>
                    this.beforeunloadHandler(e)
                );
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