<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
   table * {margin:5px}
   table {width:100%}

</style>
</head>
<body>
   <jsp:include page="../common/header.jsp"/>
   <br><br>
   <div class="content">
      <br><br>
      <div class="innerOuter">
         <h2>게시글 상세보기</h2>
         <br>
         
         <a class="btn btn-secondary" style="float:right;" href="${contextPath }/board/list/${boardCode }">목록으로</a>
         <br><br>
         
         <table id="contentArea"   align="center" class="table">
            <tr>
               <th width="100">제목</th>
               <td colspan="3">${b.boardTitle }</td>
            </tr>
            <tr>
               <th>첨부파일</th>
               <td colspan="3">
                  <a href="${b.changeName }" download="${b.originName }">${b.originName }</a>
               </td>
            </tr>
            <c:if test="${not empty b.imgList }">
               <c:forEach var="i" begin="0" end="${fn:length(b.imgList) -1}">
                  <tr>
                     <th>이미지${i+1 }</th>
                     <td colspan="3">
                        <img src="${contextPath }/resources/images/boardT/${b.imgList[i].changeName}">
                        <a href="${contextPath }/resources/images/boardT/${b.imgList[i].changeName}"
                        download="${b.imgList[i].changeName }">다운로드</a>
                     </td>
                  </tr>
               </c:forEach>
            </c:if>
            <tr>
               <th>내용</th>
               <td colspan="3">
               
               </td>
            </tr>
            <tr>
               <td colspan="4"><p style="height:150px;">${b.boardContent }</p></td>
            </tr>
         </table>
         <br>
         
         <div align="center">
            <!-- 수정하기, 삭제하기 버튼은 이글이 본인이 작성한 글일 경우에만 보여져야한다. -->
<%--             <c:if test="${not empty loginUser and loginUser.userNo eq boardWriter }"> --%>
               <a class="btn btn-primary" href="${contextPath }/board/enrollForm/${boardCode}?mode=update&bno=${b.boardNo}">수정하기</a>
               <a class="btn btn-danger" href="">삭제하기</a>
<%--             </c:if> --%>
         </div>
         <br><br>
         
         <!-- 댓글등록기능 -->
         <table id="replyArea" class="table" align="center">
            <thead>
               <tr>
                  <th colspan="2">
                     <textarea class="form-control" name="replyContent" id="replyContent" rows="2" cols="55" 
                     style="resize:none; width:100%;"></textarea>
                  </th>
                  <th style="vertical-align: middle;"><button class="btn btn-secondary" onclick="insertReply()">등록하기</button></th>
               </tr>
               <tr>
                  <td colspan="3">댓글(<span id="rcount">3</span>)</td>
               </tr>
            </thead>
            <tbody>
               <!-- 스크립트 구문으로 댓글 추가 -->
            
            </tbody>
         </table>
            <script>
               $(function(){
               selectReplyList();
                  
                  // 1초마다 댓글을 불러오겠다
//                     setInterval(selectReplyList(), 1000);
               });
            
               function insertReply(){
                  $.ajax({
                     url : "insertReply.bo",
                     data : {
                        refBno : '${b.boardNo}',
                        replyContent : $("#replyContent").val()
                     },
                     
                     success : function(result){
                        if(result == "1"){
                           alertify.alert("서비스 요청 성공", '댓글등록성공')
                           
                        }else{
                           alertify.alert("서비스 요청 실패", '댓글등록실패')
                           
                        }
                        selectReplyList();
                     },
                     complete : function(){
                        $("#replyContent").val("");
                     }
                  });
               }
         
               function selectReplyList(){
                  $.ajax({
                     url : "${contextPath}/board/reply.bo",
                     data : {bno : ${b.boardNo}},
                     dataType : "json",
                     success : function(result){
                        console.log("성공");
                        console.log(result);
                        let html = "";
                        
                        for(let reply of result){
                           html += "<tr>"
                              + "<td>" + reply.replyWriter+"</td>"
                              + "<td>" + reply.replyContent+"</td>"
                              + "<td>" + reply.createDate+"</td>"
                           + "</tr>";
                        }
                        $("#replyArea tbody").html(html);
                        $("#rcount").html(result.length);
                     },
                     error : function(){
                        console.log("댓글리스트조회용 ajax통신 실패!");
                     }
                  });
               }
         
            </script>
                  
      </div>
   </div>
   
   
   
   
   
   


   <jsp:include page="../common/footer.jsp"/>
   
   
   
   
</body>
</html>