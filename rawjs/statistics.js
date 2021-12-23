const AttributeBinding = {
    data() {
        return {
            user: null,

            searchInput: null,
            searchRes: null,

            isRenderCharts: false,
            curSelected: 7,

            datas: {}

        }
    },
    methods: {
        logout() {
            $.get("api/logout", function (res) {
                if (res == true) {
                    localStorage.removeItem("user")
                    $(location).attr('href', '/')
                }
            })
        },
        getStatistic(days) {

            this.isRenderCharts = false
            this.datas.days = days
            this.datas.userid = this.user.id
            this.curSelected = days
            let that = this
            setTimeout(function () {
                that.isRenderCharts = true
            }, 200)
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
                $.post("/query/en", {
                    word: that.searchInput
                }, function (res) {
                    // // console.log(res)
                    that.searchRes = JSON.parse(res.data)
                    // // console.log(that.searchRes)
                    $('#myModal').modal()
                })
            } else {
                $.post("/query/zh", {
                    mean: that.searchInput, limit: 5
                }, function (res) {
                    // // console.log(res)
                    that.searchRes = JSON.parse(res.data)
                    // // console.log(that.searchRes)
                    $('#myModal').modal()
                })
            }
        },
        // 拼写确认

        playAudio(isUK) {
            let wordAudio = $('#wordAudio')[0]
            if (isUK == 1) {
                wordAudio.src = this.curSpeech.uk
                wordAudio.load()
                wordAudio.play()
            } else if (isUK == 2) {
                wordAudio.src = this.curSpeech.us
                wordAudio.load()
                wordAudio.play()
            } else if (isUK == 3) {
                wordAudio.src = 'https://dict.youdao.com/dictvoice?word=' + this.searchRes.word + '&type=1'
                wordAudio.load()
                wordAudio.play()
            }
        },

        load() {

            this.getStatistic(7);
        }
    },
    computed: {
        searchWordExchange() {
            let arr = this.searchRes.exchange.split('/')
            let res = ''
            for (let i = 0; i < arr.length; i++) {
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

        this.load()
    },
}

const app = Vue.createApp(AttributeBinding)
app.component('charts', {
    props: ['datas'],
    template: `
        <div id="main"></div>
        `,
    mounted() {
        this.myChart = echarts.init(document.getElementById('main'))

        let that = this

        $.post("/statistic/getRecentData", {
            userid: that.datas.userid,
            days: that.datas.days
        }, function (res) {
            that.myChart.setOption({
                color: ['#28a745', '#FF6347'],
                series: [
                    {
                        name: '统计',
                        type: 'pie',
                        radius: '55%',
                        label: {
                            normal: {
                                show: true,
                                textStyle: {
                                    fontSize: 18
                                },
                                formatter: '{b}: {c} ({d}%)'
                            }
                        },
                        data: res.data
                    }
                ]
            })
        })

    }
})

app.mount('#app')


let handler = setInterval(function () {
    $.get("api/isLogin", function (res) {
        if (res.data !== true) {
            clearInterval(handler)
            alert("您的账号已经在别处登录！")
            $(location).attr("href", "/")
        }
    })
}, 3000)