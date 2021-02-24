<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <s:include value="include.jsp"/>
        <title>UcBusca</title>
    </head>
    <style>
        body{
            background: rgb(2,0,36);
            background: linear-gradient(90deg, rgba(2,0,36,1) 0%, rgba(9,9,121,1) 35%, rgba(0,212,255,1) 100%);
        }

        .container {
        display: inline-block;
        cursor: pointer;
        }

        .bar1, .bar2, .bar3 {
        width: 35px;
        height: 5px;
        background-color: white;
        margin: 6px 0;
        transition: 0.4s;
        }

        .change .bar1 {
        -webkit-transform: rotate(-45deg) translate(-9px, 6px);
        transform: rotate(-45deg) translate(-9px, 6px);
        }

        .change .bar2 {opacity: 0;}

        .change .bar3 {
        -webkit-transform: rotate(45deg) translate(-8px, -8px);
        transform: rotate(45deg) translate(-8px, -8px);
        }
        #texto{
            color: white;
            text-align: center;
        }
    </style>

    <body>
    <c:if test="${session.loggedin == true}">
        <a class="container" href="<s:url action="projeto2"/>" onclick="myFunction(this)" style="margin-top: 2.5%;margin-left: 2%">
            <div class="bar1"></div>
            <div class="bar2"></div>
            <div class="bar3"></div>
        </a>

        <c:if test="${session.admin == true}">
            <div style="text-align: center;">
                <c:forEach items="${info}" var="item" varStatus="loop">
                    <c:if test="${loop.index == 0}">
                        <h3 style="font-weight: bold; color: white"><c:out value="${item}" /><br></h3>
                    </c:if>
                    <c:if test="${loop.index == 1 || loop.index == 13}">
                        <p style="text-decoration: underline; color: white"><c:out value="${item}" /><br></p>
                    </c:if>
                    <c:if test="${loop.index != 0 && loop.index != 13 && loop.index != 1}">
                        <p style="color: white"><c:out value="${item}" /><br></p>
                    </c:if>
                </c:forEach>
            </div>
        </c:if>
        <c:if test="${session.admin == false}">
            <p id="texto">Não tem permissões para esta página.</p>
        </c:if>
    </c:if>
    <c:if test="${session.loggedin == false}">
        <a class="container" href="<s:url action="index"/>" onclick="myFunction(this)" style="margin-top: 2.5%;margin-left: 2%">
            <div class="bar1"></div>
            <div class="bar2"></div>
            <div class="bar3"></div>
        </a>
        <p id="texto">Não tem permissões para esta página.</p>
    </c:if>
    <c:if test="${session.loggedin == null}">
        <a class="container" href="<s:url action="index"/>" onclick="myFunction(this)" style="margin-top: 2.5%;margin-left: 2%">
            <div class="bar1"></div>
            <div class="bar2"></div>
            <div class="bar3"></div>
        </a>
        <p id="texto">Não tem permissões para esta página.</p>
    </c:if>

    </body>
</html>
