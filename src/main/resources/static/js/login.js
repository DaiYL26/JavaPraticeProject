$(document).ready(function () {

    //公用函数
    var time = 60;

    function isEmail(str){
        let reg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/;
        return reg.test(str);
    }

    function isPhone(str) {
        let reg = /^1[3456789]\d{9}$/;
        return reg.test(str)
    }

    function checkFormat(mail, pwd, pwd2) {
        if (!isEmail(mail)) {
            alert("邮箱格格式不正确")
            return false
        }

        if (pwd.length < 6 || pwd.length > 18) {
            alert("密码长度应在 6 ~ 18 ")
            return false
        }

        if (pwd !== pwd2) {
            alert("两次密码不一致")
            return false
        }

        return true
    }

    //忘记密码对话框
    //获取验证码
    $('#forgetGetBtn').click(function () {

        let mail = $('#forgetMail').val()

        if (!isEmail(mail)) {
            alert("邮箱格式不正确")
            return
        }

        if (time < 60) {
            alert("请" + time + "秒后，再试一次")
            return;
        }

        $.post("api/verify", {
            mail: mail
        }, function (res) {

            if (res.data === true) {                
                $('#forgetGetBtn').attr('disabled', true)
                
                let timer = setInterval(function () {
                    if (time == 0) {
                        time = 60
                        $('#forgetTimer').text('')
                        $('#forgetGetBtn').attr('disabled', false)
                        clearInterval(timer)
                    } else {
                        $('#forgetTimer').text(time)
                        time --
                    }
                }, 1000)
            } else {
                alert('请稍后再试~')
            }

        })
    })

    //确认按钮
    $('#forgetConfirm').click(function () {

        let mail = $('#forgetMail').val()
        let pwd = $('#forgetPwd').val()
        let pwd2 = $('#forgetPwd2').val()
        let code = $('#forgetCode').val()

        if (!checkFormat(mail, pwd, pwd2)) {
            return
        }

        $.post("api/reset", {
            mail: mail,
            newPassword: pwd,
            verifyCode: code
        }, function (res) {

            if (res.data == true) {
                alert("重置成功")
                $('#myModal').modal('hide')
            } else {
                alert("重置失败，请检查验证码或邮箱")
            }

        })
    })

    //登陆事件
    $('#loginBtn').click(function () {
        let account = $('#account').val()
        let password = $('#userPwd').val()

        $.post("api/login", {
            account: account,
            password: password
        }, function (res) {
            if (res.data !== null) {
                console.log(JSON.stringify(res.data))
                localStorage.setItem("user", JSON.stringify(res.data))
                $(location).attr("href", "/main")
            } else {
                alert(res.msg)
            }
        })
    })


    //注册事件
    $('#getCodeBtn').click(function () {
        let mail = $('#registerMail').val()

        if (!isEmail(mail)) {
            alert("邮箱格式不正确")
            return
        }

        if (time < 60) {
            alert("请" + time + "秒后，再试一次")
            return;
        }

        $.post("api/verify", {
            mail: mail
        }, function (res) {

            if (res.data === true) {
                $('#getCodeBtn').attr('disabled', true)

                let timer = setInterval(function () {
                    if (time == 0) {
                        time = 60
                        $('#registerTimer').text('')
                        $('#getCodeBtn').attr('disabled', false)
                        clearInterval(timer)
                    } else {
                        $('#registerTimer').text(time)
                        time --
                    }
                }, 1000)
            } else {
                alert('请稍后再试~')
            }

        })

    })

    //提交注册
    $('#registerConfirm').click(function () {
        let mail = $('#registerMail').val()
        let phone = $('#registerPhone').val()
        let pwd = $('#registerPwd').val()
        let pwd2 = $('#registerPwd2').val()
        let nickName = $('#registerNickName').val()
        let verifyCode = $('#verifyCode').val()

        if (!checkFormat(mail, pwd, pwd2)) {
            return
        }

        if (!isPhone(phone)) {
            alert("手机号码格式不正确")
            return
        }

        if (nickName == "" || nickName.length < 2 && nickName.length > 12) {
            alert("昵称不能为空且应为 2 ~ 12 位")
            return;
        }

        $.post("api/register", {
            mail: mail,
            phone, phone,
            password: pwd,
            nickName: nickName,
            verifyCode: verifyCode
        }, function (res) {
            if (res.data === true) {
                alert("注册成功")
                $('#myModal2').modal('hide')
            } else {
                alert(res.msg)
            }
        })

    })

    $('#CodeLoginBtn').hide()
    $('#enterCode').hide()

    $('#switcher').click(function () {
        // console.log(123)
        //进入按钮切换
        if ($('#switcher').text() == '验证码登陆') {
            $('#CodeLoginBtn').show()
            $('#enterCode').show()
            $('#enterPwd').hide()
            $('#loginBtn').hide()
            $('#switcher').text('密码登陆')
            $('#account').attr('placeholder', '输入您的邮箱')
        } else {
            $('#CodeLoginBtn').hide()
            $('#enterCode').hide()
            $('#enterPwd').show()
            $('#loginBtn').show()
            $('#switcher').text('验证码登陆')
            $('#account').attr('placeholder', '输入您的邮箱/手机')
        }
    })

    // 登陆验证码按钮
    $('#loginGetBtn').click(function () {
        let mail = $('#account').val()

        if (!isEmail(mail)) {
            alert("邮箱格式不正确")
            return
        }

        if (time < 60) {
            alert("请" + time + "秒后，再试一次")
            return;
        }

        $.post("api/verify", {
            mail: mail
        }, function (res) {

            if (res.data === true) {
                $('#loginGetBtn').attr('disabled', true)

                let timer = setInterval(function () {
                    if (time == 0) {
                        time = 60
                        $('#loginTimer').text('')
                        $('#loginGetBtn').attr('disabled', false)
                        $('#loginGetBtn').text('获取验证码')
                        clearInterval(timer)
                    } else {
                        // $('#loginTimer').text(time)
                        $('#loginGetBtn').text('请等待' + time + '秒')
                        time --
                    }
                }, 1000)
            } else {
                alert('请稍后再试~')
            }

        })
    })

    // 验证码登陆按钮
    $('#CodeLoginBtn').click(function () {

        let account= $('#account').val();
        let code = $('#loginCode').val();

        if (!isEmail(account)) {
            alert("邮箱格式错误")
            return
        }

        if (code.length != 6) {
            alert("验证码为 6 位")
            return
        }

        $.post("api/codeLogin", {
            mail: account,
            code: code
        }, function (res) {
            if (res.data !== null) {
                console.log(JSON.stringify(res.data))
                localStorage.setItem("user", JSON.stringify(res.data))
                $(location).attr("href", "/main")
            } else {
                alert(res.msg)
            }
        })
    })


})