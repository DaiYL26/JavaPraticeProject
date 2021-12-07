const Attribute = {
    data() {
        return {
            user: null,
            username: null,

            searchInput: null,
            searchRes: null,

            status: {learnCount: 0, reviewCount: 0},
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

        logout() {
            $(location).attr('href', '/')
        },

        Confirm() {
            let that = this
            console.log(123);
            $.post("/setting/updateInfo", {
                userid: that.user.id,
                username: that.username,
                studyword: that.status.learnCount,
                reviewCnt: that.status.reviewCount

            }, function (res) {
                console.log(res);
                if(res.code == 200) {
                    that.user.nickName = that.username
                    localStorage.setItem("user", JSON.stringify(that.user))
                    bs4pop.notice( res.data, {position: 'topcenter', type: 'success'} )
                } else {
                    bs4pop.notice( res.msg, {position: 'topcenter', type: 'warning'} )
                }
            })
        },

        getStatus() {
            let that = this
            $.post("/setting/getStatus", {
                userid: that.user.id
            }, function (res) {
                if (res.code == 200) {
                    let status = res.data
                    that.status = status
                } else {
                    bs4pop.notice( res.msg, {position: 'topcenter', type: 'warning'} )
                }
            })
        }
    },

    created() {
        let jsonStr = localStorage.getItem("user")
        this.user = JSON.parse(jsonStr)
        // this.user = { id: 16, mail: "877669110@qq.com", phone: "13553285743", nickName: "moonlight" }
        this.username = this.user.nickName
        this.getStatus()
    },
}
Vue.createApp(Attribute).mount('#app')