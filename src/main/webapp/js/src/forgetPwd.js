/**
 * Created with JetBrains WebStorm.
 * User: ty
 * Date: 14-4-16
 * Time: 下午2:07
 * To change this template use File | Settings | File Templates.
 */
var forgetPwd=(function(config){
    return {
        submitForm:function(form){
            config.showBlackout();
            $(form).ajaxSubmit({
                dataType:"json",
                success:function(data){
                    if(data.success){

                        $("#tip").removeClass("hidden");
                        $("#forgetPwdForm").addClass("hidden");
                        $("#emailUrl").attr("href",
                            config.emailUrls[config.getEmailDomain($("#email").val().trim())]);
                        config.hideBlackout();
                    }else{
                        config.ajaxReturnErrorHandler(data);
                    }
                },
                error:function(){
                    config.ajaxErrorHandler();
                }
            });
        }
    }
})(config);
$(document).ready(function(){
    $("#forgetPwdForm").validate({
        rules: {
            email: {
                required:true,
                email:true,
                rangelength:[6, 30]
            }
        },
        messages: {
            email: {
                required:config.ownInvalidMessage.required,
                email:config.ownInvalidMessage.email,
                rangelength:config.ownInvalidMessage.rangelength.replace("${min}",6).replace("${max}",30)
            }
        },
        submitHandler:function(form) {
            forgetPwd.submitForm(form);
        }
    });



});
