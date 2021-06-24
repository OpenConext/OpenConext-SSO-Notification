<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <style type="text/css">
        body {
            background-color: #D5D6E9;
            font-family: 'Open Sans', sans-serif;
            font-size: 14px;
            line-height: 1.8em;
            color: #10176A;
            margin: 0;
        }
        .label {
            font-style: italic;
        }

        #content {
            background-color: #F8F9FC;
            margin: 20px auto 0;
            padding: 40px 60px 70px 60px;
            display: block;
            max-width: 800px;
            overflow: visible;
        }

        ul {
            list-style: none;
            padding: 0;
        }

        .error-message {
            border: 1px solid #10176A;
            border-radius: 5px;
            margin-top: 40px;
            clear: both;
        }

        .error-message .header {
            background-color: #10176A;
            color: white;
            padding: 5px 15px;
            margin: 0;
        }

        .error-message ul {
            padding: 5px 15px;
        }

        #footer {
            max-width: 800px;
            text-align: center;
            margin: 5px auto;
            font-size: 12px;
        }

        .smiley-container {
            text-align: center;
            padding-bottom: 20px;
            clear: right;
            float: right;
            margin-top: 0;
            margin-left: 30px;
            width: 20%;
        }

        .m-smiley {
            display:inline-block;
            padding:0.16em 0.32em 0.32em;
            font-size: 40px;

            -webkit-border-radius:100%;
            -moz-border-radius:100%;
            -ms-border-radius:100%;
            -o-border-radius:100%;
            border-radius:100%;

            -webkit-transform:rotate(90deg);
            -moz-transform:rotate(90deg);
            -ms-transform:rotate(90deg);
            -o-transform:rotate(90deg);
            transform:rotate(90deg);

            background-color:#D5D6E9;
            line-height:1.1em;

            font-family:serif;
            color:#10176A;
            text-align: center;
        }

        @media only screen and (min-width: 1180px) {
            #content {
                padding: 40px 140px 70px 140px;
                max-width: 800px;
                font-size: 16px;
                width: auto;
            }

            #footer {
                font-size: 14px;
            }
        }

        @media only screen and (max-width: 1000px) {
            #content {
                padding-right: 0;
                padding-left: 0;
                width: auto;
                max-width: inherit;
                margin: 0 auto;
            }

            h1, h2, p, div {
                margin-right: 30px;
                margin-left: 30px;
            }
        }

        @media only screen and (max-width: 767px) {
            h1, h2, p, div, section {
                margin-right: 20px;
                margin-left: 20px;
            }

            #content {
                padding: 10px 15px 40px 15px;
            }
        }

        @media only screen and (max-width: 479px) {
            h1,h2, div, p, section {
                margin-right: 5px;
                margin-left: 5px;
            }

            .smiley-container {
                float: none;
                width: auto;
                margin-left: 0;
            }
        }
    </style>
    <title>Unfortunately an error occurred (${status})</title>
</head>
<body>
    <div id="content">
        <h1>Unfortunately an error occurred (${status})</h1>
        <#if message??>
            <p>${message}</p>
        </#if>
        <div class="error-message">
            <div class="header">Error details</div>
            <ul>
                <li><span class="label">Time</span>: ${timestamp?datetime}</li>
                <li><span class="label">Type</span>: ${error}</li>
                <li><span class="label">Status</span>: ${status}</li>
            </ul>
        </div>
    </div>
    <div id="footer">
    </div>
</body>
</html>