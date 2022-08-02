function like(btn, entityType, entityId) {
    $.post( /*发送post请求*/
        CONTEXT_PATH + "/like", /*提交的路径*/
        {"entityType":entityType, "entityId":entityId}, /*携带的参数*/
        function (data) { /*返回的数据*/
            data = $.parseJSON(data);
            if (data.code == 0) { /*点赞成功*/
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1?'已赞':'赞');
            } else { /*点赞失败*/
                alert(data.msg);
            }
        }
    );
}