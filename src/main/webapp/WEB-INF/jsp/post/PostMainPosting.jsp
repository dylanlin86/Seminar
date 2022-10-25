<%@page import="tw.group5.post.model.MainPostBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<HTML>
   <HEAD>
      <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8"/>
        <link rel="stylesheet" href="styles/Topic.css">
      <TITLE>發布貼文</TITLE>
      <style>
.imgss {
    width: 300px;
    height: 200px;
    padding: 5px 5px 5px 5px;
    margin: 10px;
}


       fieldset {
    width: 700px;
    border: 1px solid #acd6ff;
    border-radius: 15px;
    opacity: 0.85;
    margin: auto;
    background: #f7f4dd;
} 
      </style>
      
   </HEAD>

  <BODY>
  
  <%@ include file="../admin/AdminstyleHead.jsp" %>
   
        <fieldset>
    <FORM ACTION="${pageContext.request.contextPath}/group5/admin/MainPosting.controller" 
            enctype='multipart/form-data' method="POST"onsubmit="return checkip()">
            
            
        <H1 ALIGN="CENTER">發布貼文</H1> 
        <div class="st1">
                <label for="" class="t1" >貼文分類:</label>
                <label >
                    <input type="radio" name="postTypeName" value="公告" id="" required oninvalid="setCustomValidity('請輸入帳號')" oninput="setCustomValidity('')">公告
                </label>
                
                <label>
                    <input type="radio" name="postTypeName" value="分享" id="" checked>分享
                </label>
                
                <label>
                    <input type="radio" name="postTypeName" value="問題" id="">問題
                </label>
        </div> 
       
        <div class="st1">
            <label for="data1" class="t1">帳號:</label>${memberAccount}
          
       
        </div>
        
        <div class="st1">
            <label for="data3" class="t1">標題:</label>
            <INPUT TYPE="TEXT" id=data3 name="title" 
            required oninvalid="setCustomValidity('請輸入標題')" oninput="setCustomValidity('')"><BR>
        </div>
   
        <div class="st1">
            <label for="content1" class="t1">內容:</label>
            <textarea name="content" id="content1" cols="40" rows="10" 
            required oninvalid="setCustomValidity('請輸入內容')" oninput="setCustomValidity('')"></textarea>
        </div>
                  
       請選擇圖片(最多五張，每張2MB以下):<input id="file1" type="file" class="btn btn-outline-primary" name="Filename2" multiple="multiple" 
                            accept="image/*" onchange="checkip()">           
            <div id="imgs"></div>  
    
        <script src="/group5/js/jquery.min.js"></script>
        <script src="js/images.js"></script>

    
        <div class="sub">       
            <input type="submit" name="submit" class="btn btn-outline-success"  value="送出">
        </div>
     </FORM>
     
     <form name="updateForm" action="MainPost.return" method="GET">
       <!--   <input type="hidden" name="todo" value="return"> -->
       <div class="sub"> 
       <input type="submit" name="returns" class="btn btn-outline-primary" value="返回首頁">
             </div>
    </form>
    </fieldset>
    
    <%@ include file="../admin/AdminstyleFoot.jsp"%>
    
    </BODY>
</HTML>
