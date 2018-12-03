	expSubjectPattern = WScript.Arguments(0)
	expBodyPattern = WScript.Arguments(1)
	numberOfAttempts = WScript.Arguments(2)
	
	numberOfAttempts_integer = cint(numberOfAttempts)
	
	If expSubjectPattern = "" and expBodyPattern ="" Then
		EmailFound = False
	else
		Set olApp=CreateObject("Outlook.Application")
		Set olMAPI=olApp.GetNameSpace("MAPI") 
		Set oFolder = olMAPI.GetDefaultFolder(6)
	
	
		'Only scan through intial 10 emails(if it contains more than 10 email) with a maximum of 4 attempts(to let the email arrive into inbox)
		EmailFound= False
		c=0
		Do	
			Set allEmails = oFolder.Items
			allEmails.Sort "[ReceivedTime]",1
			
			emailCount = allEmails.Count
			If emailCount < 10 Then
                iterLimit = emailCount
            Else
                iterLimit = 10
            End If
            
			Set currEmail = allEmails.GetFirst
			
			For i = 1 To iterLimit Step 1
				If currEmail.Unread = True Then
				
					currSubject =  currEmail.subject					
					emailBody =  currEmail.Body	
					'msgbox emailBody			
					If Instr(1,currSubject,expSubjectPattern,vbTextCompare) Then
						If Instr(1,emailBody,expBodyPattern,vbTextCompare) Then
							
							emailToList=currEmail.To
							emailCCList=currEmail.CC
							emailFrom= currEmail.sender
							emailAttachmentCount=currEmail.Attachments.Count
							If emailAttachmentCount > 0 Then
							emailAttachmentName=currEmail.Attachments.Item(1).FileName
							End If
							EmailFound= True
							currEmail.Unread = False
							Exit Do 
						End If
					End If		
					
				End If 
				Set currEmail = allEmails.GetNext
			Next
			c=c+1	
			WScript.sleep(10000)		
		Loop While c < numberOfAttempts_integer
	End If
	
	If EmailFound = True Then
		WScript.echo emailFrom
		WScript.echo emailToList
		WScript.echo emailCCList
		WScript.echo currSubject
		WScript.echo emailAttachmentCount
		WScript.echo emailAttachmentName
		WScript.echo emailBody

	else
		WScript.echo "EmailNotFound"
	End If	
	
	'------------------Clear outlook objects -------------------
	Set currEmail =  Nothing 
	Set olApp=Nothing
	Set olMAPI=Nothing
	Set oFolder=Nothing
	Set allEmails=Nothing