<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Access-Control-Allow-Origin" content="*">
        <title>监控内存</title>
        <script type="text/javascript" src="../js/jquery-1.11.1.js"></script>
        <style type="text/css">
.center {
    width: 380px;
}

.uiTable {
    border: 1px solid #E8E7E7;
    border-collapse: separate;
    border-spacing: 0;
    width: 430px;
}

.uiTable th {
    height: 30px;
    background: #F1F1F1 none;
    line-height: 30px;
    font-size: 12px;
}

.uiTable td {
    height: 30px;
    text-align: center;
    line-height: 30px;
    font-size: 12px;
}

.shutdown {
    width: 67px;
    height: 22px;
    padding: 0;
    border: 0 none;
    background-position: 0 -147px;
    line-height: 22px;
    text-align: center;
    color: #FFF;
    font-family: "Arail";
    font-weight: bold;
    cursor: pointer;
    background: url(../images/virtualblock.png) 0 -170px no-repeat;
}

.hide {
    display: none;
}
.alertWindowContent h1,p{text-align: center;font-size: 18px;font-weight: bolder;}
.alertWindowContent input{width: 100px; height: 50px;cursor: pointer;-webkit-border-radius: 5px;-moz-border-radius: 5px;border-radius: 5px;}
</style>
<script type="text/javascript">
function submit(obj){
$.get("../manage/monitor.do",function(data,status){
    $(obj).parent().parent().find(".number").text(data);
  });
}
</script>
    </head>
    <body
        style="background: #F8F8F8; padding-top: 100px; padding-left: 500px;">
        <div class="center">
            <table class="uiTable">
                <thead>
                    <tr>
                        <th width="270px;">
                            内存中消息数
                        </th>
                        <th width="130px;">
                            操作
                        </th>
                    </tr>

                </thead>
                <tbody>
                    <tr>
                        <td><span style="color:red;" class="number"></span>
                        </td>
                        <td>
                            <input type="button" class="shutdown" value="刷新"
                                onclick="submit(this)" />
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </body>
</html>