$(document).ready(function(){
    $("#captchaRefresh").click(function(){
        $(this).find("img").attr("src","s/captcha.jpg?"+Math.random());
        return false;
    });

    $("#loginForm").validate({
        rules: {
            username: {
                required:true,
                email:true,
                rangelength:[6, 30]
            },
            password:{
                required:true,
                rangelength:[6, 120]
            }/*,
            captcha:{
                required:true,
                rangelength:[4,4]
            }*/
        },
        messages: {
            username: {
                required:config.ownInvalidMessage.required,
                email:config.ownInvalidMessage.email,
                rangelength:config.ownInvalidMessage.rangelength.replace("${min}",6).replace("${max}",30)
            },
            password:{
                required:config.ownInvalidMessage.required,
                rangelength:config.ownInvalidMessage.rangelength.replace("${min}",6).replace("${max}",120)
            }/*,
            captcha:{
                required:config.ownInvalidMessage.required,
                rangelength:config.ownInvalidMessage.rangelength.replace("${min}-${max}",4)
            }*/
        }
    });
});