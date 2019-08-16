<%--
  Created by IntelliJ IDEA.
  User: zhouda
  Date: 2019/8/13
  Time: 14:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <title>用户登录</title>
    <meta content="width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=0" name="viewport">
    <meta content="yes" name="apple-mobile-web-app-capable">
    <meta content="black" name="apple-mobile-web-app-status-bar-style">
    <meta content="telephone=no" name="format-detection">
    <link href="/css/style.css" rel="stylesheet" type="text/css">
    <script type="text/javascript" src="/js/jquery.min.js"></script>
    <script type="text/javascript" src="/js/jquery.qrcode.min.js"></script>
</head>
<script type="text/javascript">
    $(function () {
        $(document).on('click','#signin',function () {
            $.post("loginJdy",{"username":$("#uname").val()},function (data) {
                if(data.err){
                    alert(data.err);
                }else{
                    location="https://www.jiandaoyun.com/sso/custom/5d5116279a946019768eb746/iss";
                }
            });
        });
    })
</script>
<body>


<section class="aui-flexView">
    <header class="aui-navBar aui-navBar-fixed">
        <a href="javascript:;" class="aui-navBar-item">
            <i class="icon icon-return"></i>
        </a>
        <div class="aui-center">
            <span class="aui-center-title"></span>
        </div>
        <a href="javascript:;" class="aui-navBar-item">
            <i class="icon icon-sys"></i>
        </a>
    </header>
    <section class="aui-scrollView">
        <div class="aui-sign-header">
            <h1>登录简道云</h1>
        </div>

        <div class="aui-flex-from">
            <div class="aui-flex b-line">
                <div class="aui-flex-box">

                    <i class="icon icon-phone"></i>
                    <input type="text" name="username" placeholder="请输入在简道云的成员编号" id="uname">

                </div>
            </div>

            <div class="aui-flex-forget">
                <%--<a href="javascript:;">忘记密码</a>--%>
            </div>
            <div class="aui-button-sign">
                <button id="signin">登录</button>
            </div>
        </div>

    </section>
</section>



</body>

</html>
