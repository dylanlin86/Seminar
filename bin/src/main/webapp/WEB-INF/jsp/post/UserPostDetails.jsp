<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8" />
<link rel="stylesheet" href="styles/Topic.css">
<title>${queryOne.title}</title>
<script src="/group5/js/jquery.min.js"></script>

<script src="//cdn.jsdelivr.net/npm/sweetalert2@11"></script>

<script type="text/javascript">
$(function(){
    $("#likemainpost").on("click",function(){
        var mainPostNo = $(this).prev().val();
        let mainPostBean={"mainPostNo":mainPostNo};
        $.ajax({
            type: "put",
            url:'/group5/LikesAJAX',
            data: mainPostBean,
            dataType:'json',
           
            success: function(data){
                  console.log(data);
                  $("#likemainpost").text(data.likeNumber);
                 //location.reload();
            }
        });
    });
});

$(function(){
  $(".replyreport").on("click",function(){
	  let replyNo = $(this).prev().siblings(".replyNo").val();
	  //let rBean={"replyNo":replyNo};
	  //console.log(replyNo);
	  
	  
      (async () => {

          const { value: text } = await Swal.fire({
        	title:'檢舉原因',
            input: 'textarea',
            inputLabel: '',
            inputPlaceholder: '請輸入內容',
            inputAttributes: {
              'aria-label': 'Type your message here'
            },
            showCancelButton: true
          })
          if (text) {
              
        	  replyreport(replyNo,text)
  
              Swal.fire("已檢舉").then((result)=>{
                  if(result.isConfirmed){
                      
                      
                     
                  }
              });
          }

          })()
	  

  })
  
  
  function replyreport(replyNo,text){
	  
      $.ajax({
          type: "put",
          url:'/group5/ReplyReportAJAX/'+replyNo+'/'+text,
          success: function(){
              console.log("ok");
          }
      });
  }
  
  
  
  
  
});




</script>
<style>
.imgpostdetails {
	width: 300px;
	padding: 5px 5px 5px 5px;
	margin: 10px;
}

.imgheadstickers {
        width: 150px;
        height: 120px;
        padding: 2px 2px 2px 2px;
        margin: 2px;
}

.table1 {
	width: 600px;
	table-layout: fixed;
	margin-left: auto;
	margin-right: auto;
	background-color: #f7f4dd;
	top: -100px;
	left: 100px;
}

.column1 {
	width: 200px;
	text-align: center;
	vertical-align: bottom;
	background-color: #f7f4dd;
}

.column2 {
	width: 800px;
	text-align: left;
	vertical-align: bottom;
	background-color: #f7f4dd;
}

.column3 {
	width: 200px;
	text-align: center;
	vertical-align: top;
	background-color: #f7f4dd;
}

.column4 {
	width: 800px;
	text-align: center;
	background-color: #f7f4dd;
}

.imgss {
    width: 300px;
    height: 200px;
    padding: 5px 5px 5px 5px;
    margin: 10px;
}

.content {
    /* border: 1px solid;*/
    /* width: 800px;*/
    /* height: 400px; */
    background-color: #f7f4dd;
    /* margin: auto; */
    word-wrap: break-word;
}



</style>
</head>
<body>
	<%@ include file="../admin/FrontStageHead.jsp"%>
<!--                    找會員帳號  與是否禁止發言-->
	<input type="hidden" class="usermute" value="${loginMember.memberDetail.mute}">
	<input type="hidden" class="useraccount" value="${loginMember.memberAccount}">
	<br>
	<br>
	<br>
	<div class="container">
		<div class="row">
			<div class="col-lg-12">
				<table class="table-bordered table1">
					<tr>
						<td class="column1"></td>
						<td class="column4">
							<H1 ALIGN=CENTER>${queryOne.title}</H1>
						</td>
					</tr>
					<tr>
						<td class="column3">帳號:${queryOne.account}<br>
						<img class="imgheadstickers" src="${queryOne.postPhoto}"></td>
						<td>
							<div class="content">

								<P>發布時間:${queryOne.addtime}</P>
								<p class="content">${queryOne.content}</p>

								<c:forEach var="image" items="${allImages}">
									<img class="imgss" src="${image}">
								</c:forEach>

								<br> <input type="hidden" id="mainPostNo" name="mainPostNo"
									value="${queryOne.mainPostNo}">
								<button type="submit" name="likenumber" id="likemainpost"
									class="btn btn-link fa-regular fa-thumbs-up fa-1x">${queryOne.likeNumber}</button>
                                <button type="submit" class="btn btn-link fa-solid fa-bookmark fa-1x favorite">收藏</button>
                                <script>
                                $(function(){
                                	console.log($(".usermute").val());
									    $(".favorite").on("click",function(){
									        var mainPostNo = $("#mainPostNo").val();
									        $.ajax({
									            type: "POST",
									            url:'/group5/FavoritePostAJAX',
									            data: {"mainPostNo":mainPostNo},
									            dataType:'text',
									           
									            success: function(data){
									            	console.log(data);
									            	var icon = '', title = '';
									            	
									            	switch(data){
									            	  case "已加入收藏" : { icon ='success'; title = data; break;}
									            	  case "已經加入過收藏" :{ icon = 'error'; title = data; break;}
									            	  default:{ icon = 'error'; title = data; break;}
									            	}
									            	
									            	  Swal.fire({
                                                          icon: icon,
                                                          title: title,
                                                          showConfirmButton: false,
                                                          timer: 1000
                                                        })
									            }
									        });
									    });
									});
                                </script>
							</div>
						</td>
					</tr>

					<c:forEach var="onereply" items="${allReply}">
						<tr>
							<td class="column3">帳號:${onereply.replyAccount}<br>
							<img class="imgheadstickers" src="${onereply.replyPhoto}"></td>
							<td>
								<div class="content">
									<p>回復時間:${onereply.replyTime}</p>
									<p>${onereply.replyContent}</p>
									<c:forEach var="onewReplyImage" items="${onereply.r_imagess}">
										<img class="imgss" src="${onewReplyImage}">
									</c:forEach>

									<br> <input type="hidden" id="mainPostNo"
										name="mainPostNo" value="${queryOne.mainPostNo}"> 
										<input type="hidden" class="replyNo" name="replyNo" value="${onereply.replyNo}">
									<button type="submit" name="replylikenumber"
										id="${onereply.replyNo}" class="btn btn-link fa-regular fa-thumbs-up fa-1x"
										value="${onereply.replyLikeNumber}">${onereply.replyLikeNumber}</button>
										
                                    <input type="hidden" class="replyNo" name="replyNo" value="${onereply.replyNo}">
									<button type="submit" style="border:none" class="btn btn-outline-danger replyreport fa-solid fa-shield-halved fa-1x"
                                                value="${onereply.replyLikeNumber}">檢舉</button>
										
                                    
							<script type="text/javascript">
							
							
    $(function () {
        var onereply = document
            .getElementById("${onereply.replyNo}");

        onereply.onclick = function (event) {
            var replyNo = $(this).prev()
                .val();
            console.log(replyNo);
            let ReplyPostBean = {
                "replyNo": replyNo
            };
            $.ajax({
                type: "put",
                url: '/group5/ReplyLikesAJAX',
                data: ReplyPostBean,
                dataType: 'json',
                success: function (
                    data) {
                    console
                        .log(data);
                    document
                        .getElementById("${onereply.replyNo}").innerText = data.replyLikeNumber
                }
            });
        }
                  
    });
    

</script>
								</div>
							</td>
						</tr>
					</c:forEach>
					<form action=/group5/ReplyPost method="post" onsubmit="return checkip()"
						enctype='multipart/form-data'>
						<input type="hidden" name="mainPostNo"
							value="${queryOne.mainPostNo}">
							</tr>
							<td class="column3"><img class="imgheadstickers" src="${postPhoto}"></td>
							<td><div class="content">

									<textarea name="replyContent" id="content1" cols="65" rows="10"
										required oninvalid="setCustomValidity('請輸入內容')"
										oninput="setCustomValidity('')"></textarea>

								</div> 請選擇圖片(最多五張):<input id="file1" type="file"
								class="btn btn-outline-primary" name="replyfile"
								multiple="multiple" accept="image/*" onchange="checkip()">
								<div id="imgs"></div> <script src="/group5/js/jquery.min.js"></script>
								<script src="js/images.js"></script>
								
								<div class="row g-3">
                                            <div class="col-md">
                                                <div class="form-floating">
                                                    <input type="submit" class="btn-solid-reg replycontent"
                                                        id="floatingInputGrid" value="發表回復"> 
                                                        <label style="color:#FF0000" for="floatingInputGrid"></label>
                                                </div>
                                            </div>
                                            <div class="col-md">
                                                <div class="form-floating">
                                                <a class="btn-solid-reg " href="/group5/UserPostAll">返回</a>
                                                
                                                </div>
                                            </div>
                                            
                                             <div class="col-md">
                                                <div class="form-floating">
                                                <button type='submit'  style='border:none' class='btn btn-success as-button onekeyinput'>一健輸入</button>
                                                
                                                </div>
                                            </div>
                                            
                                        </div>
								
								</td>
								</tr>
								
					                           <script>
					                           $(".onekeyinput").on('click', function(event){
					                               event.preventDefault();
					                              
					                               $("#content1").val("運動虔敬量不要空腹，會建議吃點蛋白巧克力，運動後再補充一杯高蛋白。");
					                              
					                           });
					                           
					                           
					                           $(function(){
					                        	   //var usermute = $(".usermute").val();
                                                   var useraccount= $(".useraccount").val();
                                                   console.log(useraccount);
                                                   if(useraccount){
					                        	   whetherToMute(useraccount)
                                                   }else{
                                                       console.log("未登入");
                                                       $(".replycontent").attr('disabled',true);
                                                       $(".replycontent").next().text('請登入或加入會員');
                                                       $("#content1").prop("readonly",true)
                                                   }
					                           
					                           });
					           
					                           function whetherToMute(useraccount){
					                               $.ajax({
					                                   type: "Post",
					                                   url: "/group5/WhetherToMute/"+ useraccount,
					                                   dataType : 'text',
					                                   success: function(data){
					                                	   console.log(data);
					                                	   if(data == 1){
	                                                           console.log("禁止發言");
	                                                           $(".replycontent").attr('disabled',true);
	                                                           $(".replycontent").next().text('禁止發言，請聯絡管理員');
	                                                           $("#content1").prop("readonly",true)
	                                                       }
					                                	   
					                                       
					                                   }
					                               });
					                           }
					           
					                           
					                           
					                           
					           
                                                   
                                                   $(".replycontent").on('click', function(event){
                                                	   
                                                       var rs = false;
                                                       $("#content1").each(function(){
                                                           if($(this).val()==""){
                                                               console.log('請輸入內容');
                                                               Swal.fire({
                                                                   title:'請輸入內容',
                                                                   icon:'warning'
                                                               });
                                                               
                                                               $("#.replycontent").next().text('請輸入內容');
                                                               rs = true;
                                                           }
                                                       });
                                                       if(rs)return;
                                                       
                                                   });
                                                   
                                                   
                                                   </script>
					
					
					</form>
           
				</table>
			</div>
		</div>
	</div>
	<div>
	   
	</div> 
<!-- 	<form name="updateForm" action="MainPost.return" method="GET"> -->
	
<!-- 		<div class="sub"> -->
<!-- 			<input type="submit" name="returns" class="btn btn-outline-primary" -->
<!-- 				value="返回首頁" margin="auto"> -->
<!-- 		</div> -->
<!-- 	</form> -->
    

    <script src="https://kit.fontawesome.com/f795d981d8.js" crossorigin="anonymous"></script>
 

	<%@ include file="../admin/FrontStageFoot.jsp"%>
</body>
</html>