ShareName	 	=	WScript.Arguments(0)
absFolderPath	=	WScript.Arguments(1)

If shareName = "" OR absFolderPath = "" Then
	WScript.echo "SharingFailed"

else

	      
    'Shared the current Run Results Folder 
    Set objShell = CreateObject("Shell.Application")
    
    objShell.ShellExecute "cmd.exe", "/k net share "&ShareName&"="&CHR(34)&absFolderPath&CHR(34), "", "runas",0
   	
 	WScript.echo "SharingPassed"
End If