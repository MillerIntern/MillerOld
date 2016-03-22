


function showDialog(val){
                var whitebg = document.getElementById("white-background");
                var dlg = document.getElementById(val);
                whitebg.style.display = "block";
                dlg.style.display = "block";

                
                //dlg.style.left = (winWidth/2) - 480/2 + "px";
                //dlg.style.top = "150px";
            }

function dlgBtn(val){
                var whitebg = document.getElementById("white-background");
                var dlg = document.getElementById("dlgbox");
                whitebg.style.display = "none";
                dlg.style.display = "none";
				
				if(val == 'add'){
					showDialog('dlgbox_add');
					dlg.close();
					
				}else if(val == 'edit'){
					showDialog('dlgbox_edit');
					
					
				}
				else if(val == 'normal')
				{
					window.location="costSheet.html";
				}
				else if(val == 'excel')
				{
					showDialog('dlgbox_excel');	
				}
				else if(val == 'back')
				{
					document.location.href='homepage.html';
				}
				else if(val == 'back_add')
				{
					document.location.href='proposalMenu.html';
						
				}
				
            }