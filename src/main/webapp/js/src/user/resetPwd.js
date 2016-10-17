/**
 * Created with JetBrains WebStorm.
 * User: ty
 * Date: 14-3-3
 * Time: 下午3:30
 * To change this template use File | Settings | File Templates.
 */
var resetPwd=(function(config){

    return {

        submitForm:function(form){
            config.showBlackout();
            $(form).ajaxSubmit({
                dataType:"json",
                success:function(data){
                    if(data.success){
                        config.hideBlackout();
                        $().toastmessage("showSuccessToast",config.message.optSuccess);
                        $(form).resetForm();
                    }else{
                        config.ajaxReturnErrorHandler(data);
                    }
                },
                error:function(){
                    config.ajaxErrorHandler();
                }
            });
        }
    };
})(config);

$(document).ready(function(){
    //提交
    $("#resetPwdForm").validate({
        rules:{
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
        messages:{
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
            resetPwd.submitForm(form);
        }
    });

});

