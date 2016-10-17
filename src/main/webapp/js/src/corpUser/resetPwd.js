/**
 * Created with JetBrains WebStorm.
 * User: ty
 * Date: 14-4-22
 * Time: 上午10:57
 * To change this template use File | Settings | File Templates.
 */

$(document).ready(function(){
    $("#resetPwd").click(function(){
        $("#popWindowPwd,#blackout").removeClass("hidden");
        $("#blackout .loading").addClass("hidden");

        return false;
    });

    $("#resetPwdForm").validate({
        rules: {
            oldPassword:{
                required:true,
                rangelength:[6, 20]
            },
            newPassword:{
                required:true,
                rangelength:[6, 20]
            },
            newConfirmPwd:{
                equalTo:"#newPassword"
            }
        },
        messages: {
            oldPassword:{
                required:config.ownInvalidMessage.required,
                rangelength:config.ownInvalidMessage.rangelength.replace("${min}",6).replace("${max}",20)
            },
            newPassword:{
                required:config.ownInvalidMessage.required,
                rangelength:config.ownInvalidMessage.rangelength.replace("${min}",6).replace("${max}",20)
            },
            newConfirmPwd:{
                equalTo:config.ownInvalidMessage.equalTo
            }
        },
        submitHandler:function(form) {
            $(form).ajaxSubmit({
                dataType:"json",
                success:function(data){
                    if(data.success){
                        $().toastmessage("showSuccessToast",config.message.optSuccess);
                        $("#popWindowPwd,#blackout").addClass("hidden");
                        $("#blackout .loading").removeClass("hidden");
                        $(form).clearForm();
                    }else{
                        $().toastmessage("showSuccessToast",config.message.optError);
                    }
                },
                error:function(){
                    $().toastmessage("showSuccessToast",config.message.networkError);
                }
            });
        }
    });

    $("#closePopWindowPwd,#blackout").click(function(){
        $("#popWindowPwd,#blackout").addClass("hidden");
        $("#blackout .loading").removeClass("hidden");
        $("#resetPwdForm").clearForm();
        return false;
    })
});
