/**
 * Created with JetBrains WebStorm.
 * User: ty
 * Date: 14-3-3
 * Time: 下午3:30
 * To change this template use File | Settings | File Templates.
 */
var register=(function(config){

    return {
        emailExist:function(){
            var emailEl=$("#registerEmail");
            emailEl[0].setCustomValidity(config.message.emailExist);
            emailEl.data("exist",true);
        },
        emailNotExist:function(){
            var emailEl=$("#registerEmail");
            emailEl.data("exist",false);
            emailEl[0].setCustomValidity("");
        },
        checkEmail:function(email){
            var me=this;
            $.ajax({
                url:config.ajaxUrls.emailExistOrNot,
                dataType:"json",
                type:"post",
                data:{
                    email:email
                },
                success:function(data){

                    if(data.success&&!data.unique){
                        me.emailExist();
                    }else{
                        me.emailNotExist();
                    }
                },
                error:function(){
                    me.emailNotExist();
                }
            })
        },
        formSubmitHandler:function(){
            var me=this;

            //使用延迟对象
            $.when((function(email){
                    return me.checkEmail(email);
            })($("#registerEmail").val().trim())).then(
                function(){
                    if(!$("#registerEmail").data("exist")){
                        me.submitForm();
                    }
                }
            );
        },
        submitForm:function(form){
            config.showBlackout();
            $(form).ajaxSubmit({
                dataType:"json",
                success:function(data){
                    if(data.success){
                        $("#registerForm").addClass("hidden");
                        $("#tip").removeClass("hidden");
                        $("#emailUrl").attr("href",
                            config.emailUrls[config.getEmailDomain($("#registerEmail").val().trim())]);
                        config.hideBlackout();
                    }else{
                        config.ajaxReturnErrorHandler(data);
                    }
                },
                error:function()//noinspection BadExpressionStatementJS
                {
                    config.ajaxErrorHandler();
                }
            });
        }

    };
})(config);

$(document).ready(function(){

   /* //邮箱唯一性验证
    $("#registerEmail").blur(function(){
        register.checkEmail($(this).val().trim());

    });
    */
    //提交
    $("#registerForm").validate({
        rules:{
            email:{
                required:true,
                email:true,
                remote:config.ajaxUrls.emailExistOrNot,
                rangelength:[6, 30]
            },
            fullname:{
                required:true,
                maxlength:32
            },
            organization:{
                required:true,
                maxlength:120
            },
            mobile:{
                required:true,
                maxlength:20
            },
            address:{
                required:true,
                maxlength:64
            },
            password:{
                required:true,
                rangelength:[6, 20]
            },
            confirmPwd:{
                equalTo:"#registerPwd"
            },
            agree:"required"
        },
        messages:{
            email:{
                required:config.ownInvalidMessage.required,
                email:config.ownInvalidMessage.email,
                remote:config.ownInvalidMessage.emailExist,
                rangelength:config.ownInvalidMessage.rangelength.replace("${min}",6).replace("${max}",30)
            },
            fullname:{
                required:config.ownInvalidMessage.required,
                maxlength:config.ownInvalidMessage.maxlength.replace("${max}",32)
            },
            organization:{
                required:config.ownInvalidMessage.required,
                maxlength:config.ownInvalidMessage.maxlength.replace("${max}",120)
            },
            mobile:{
                required:config.ownInvalidMessage.required,
                maxlength:config.ownInvalidMessage.maxlength.replace("${max}",20)
            },
            address:{
                required:config.ownInvalidMessage.required,
                maxlength:config.ownInvalidMessage.maxlength.replace("${max}",64)
            },
            password:{
                required:config.ownInvalidMessage.required,
                rangelength:config.ownInvalidMessage.rangelength.replace("${min}",6).replace("${max}",20)
            },
            confirmPwd:{
                equalTo:config.ownInvalidMessage.equalTo
            },
            agree:config.ownInvalidMessage.agree
        },
        submitHandler:function(form) {
            register.submitForm(form);
        }
    });

    //显示协议
    $("#agreement").click(function(){
        var href=$(this).attr("href");

        $.ajaxSetup ({
            cache: false //关闭AJAX相应的缓存
        });

        $("#blackout").removeClass("hidden");
        $("#agreementContent").load(href+" #agreementBody",function(){
            $("#popWindow").removeClass("hidden");
            $("#blackout .loading").addClass("hidden");
            //$(window).scrollTop(0);
        });

        return false;
    });

    $("#agree").click(function(){
        $("#popWindow,#blackout").addClass("hidden");
        $("#blackout .loading").removeClass("hidden");
        $("#agreeInput").prop("checked",true);
    });

    $("#closePopWindow,#blackout").click(function(){
        $("#popWindow,#blackout").addClass("hidden");
        $("#blackout .loading").removeClass("hidden");

        return false;
    });


    /*$("#registerForm").submit(function(){
        //register.formSubmitHandler();

        return false;
    });*/
});

