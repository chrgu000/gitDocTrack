<!DOCTYPE html>
<#include "../../common/common.ftl">
<html>
<head>
<@header />
    <link rel="stylesheet" href="/common/plugins/jquery-weui/css/jquery-weui.css">
    <style>
        .report-header {
            padding: 5px 0;
        }

        .div-height {
            height: 35px;
            padding: 5px 15px;
        }

        .sbbmdiv-height {
            padding: 5px 15px;
        }

        .report-title {
            text-align: center;
            font-size: 21px;
            font-weight: 500;
            margin: 0 15%;
        }

        .cells-font-size {
            font-size: 18px;
            margin-top: 0.8em;
        }
    </style>
</head>

<body ontouchstart>
<header class='report-header'>
    <h2 class="report-title">线路累计客流预测条件筛选</h2>
</header>
<div class="weui-cells weui-cells_form cells-font-size" id="mainForm">

    <!--<div class="weui-cell weui-cell_select div-height">
        <div class="weui-cell__hd"><label class="weui-label">线路1</label></div>
        <div class="weui-cell__bd">
            <input class="weui-input" type="text" id="station1">
        </div>
    </div>-->
    <div class="weui-cell weui-cell_select div-height">
        <div class="weui-cell__hd"><label class="weui-label">日期</label></div>
        <div class="weui-cell__bd">
            <input class="weui-input" type="text" data-toggle='date' id='input-calender' placeholder='默认为今天'/>
        </div>
    </div>
    <div class="weui-cell weui-cell_select sbbmdiv-height">
    </div>
    <div class="weui-btn-area">
        <a class="weui-btn weui-btn_primary" href="javascript:btnSubmit()" id="showTooltips">确定</a>
    </div>
</div>
<@bottom />

<script src="/${PATH}/js/passengerflow/line/select_totalForecast.js?time=New Date()"></script>

</body>
</html>