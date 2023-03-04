<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>마이페이지</title>
</head>
<body>
	<jsp:include page="../common/header.jsp"/>
	<br><br>
	
	<div class="content">
	
		<br><br>
		
		<div class="innerOuter">
			<h2>마이페이지</h2>
			<br>
			
			
			<form action="update.me" method="post">
				 <div class="form-group">
                    <label for="userId">* ID : </label>
                    <input type="text" class="form-control" id="userId" value="${loginUser.userId }" name="userId" readonly> <br>

                    <label for="userName">* Name : </label>
                    <input type="text" class="form-control" id="userName" value="${loginUser.userName }" placeholder="Please Enter Name" name="userName" required> <br>

                    <label for="email"> &nbsp; Email : </label>
                    <input type="text" class="form-control" id="email" value="${loginUser.email }" placeholder="Please Enter Email" name="email"> <br>

                    <label for="age"> &nbsp; Age : </label>
                    <input type="number" class="form-control" id="age" value="${loginUser.age }" placeholder="Please Enter Age" name="age"> <br>

                    <label for="phone"> &nbsp; Phone : </label>
                    <input type="tel" class="form-control" id="phone" value="${loginUser.phone }" placeholder="Please Enter Phone (-없이)" name="phone"> <br>
                    
                    <label for="address"> &nbsp; Address : </label>
                    <input type="text" class="form-control" id="address" value="${loginUser.address }" placeholder="Please Enter Address" name="address"> <br>
                    
                    <label for=""> &nbsp; Gender : </label> &nbsp;&nbsp;
                    <input type="radio" id="Male" value="M" name="gender">
                    <label for="Male">남자</label> &nbsp;&nbsp;
                    <input type="radio" id="Female" value="F" name="gender">
                    <label for="Female">여자</label> &nbsp;&nbsp;
                    
                    <script>
                		$(function(){
                			//gender 필드값 선택 안될수도 있음
                			if("${loginUser.gender}" != ""){ //들어온 gender값이 빈값이 아닐때
                				//빈값 체크를 안해주면, 에러가 발생하고 있음 -> 꼭 빈값 체크해줘야함(안해주면 아래에 다른 스크립트는 아예 실행안될것임)
                				$("input[value=${loginUser.gender}]").prop("checked",true);
                			}
                		});    
                    </script>
                </div> 
                <br>
                <div class="btns" align="center">
                	<button class="btn btn-primary">수정하기</button>
                	<button type="button" class="btn btn-danger" data-toggle="modal"
                	data-target="#deleteForm">회원탈퇴</button>
                </div>
			</form>
		</div>
		<br><br>
		
		<!-- 회원탈퇴 버튼 클릭 시, 보여질 Modal창 -->
		<div class="modal fade" id="deleteForm">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<!-- 모달 해더 -->
				<div class="modal-header">
					<h4 class="modal-title">회원탈퇴</h4>
					<button type="button" class="close" data-dismiss="modal">&times;</button>
				</div>
				
				<form action="delete.me" method="post">
					<!-- 모달 바디 -->
					<div class="modal-body" align="center">
					
						<div align="center">
							탈퇴 후 복구가 불가능합니다.<br>
							정말로 탈퇴하시겠습니까?<br>
						</div>
					
						<label for="userPwd" class="mr-sm-2">Password : </label>
						<input type="password" class="form-controll mb-2 mr-sm-2" placeholder="Enter PWD"
						id="userPwd" name="userPwd"><br>
					</div>
					
					<!-- 모달 푸터 -->
					<div class="modal-footer">
						<button type="submit" class="btn btn-danger">탈퇴하기</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	</div>
	
	<jsp:include page="../common/footer.jsp"/>
	
</body>
</html>