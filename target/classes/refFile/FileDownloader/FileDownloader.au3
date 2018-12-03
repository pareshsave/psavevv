#cs ----------------------------------------------------------------------------
MsgBox(262144, 'Debug line ~' & @ScriptLineNumber, 'Selection:' & @CRLF & '#cs' & @CRLF & @CRLF & 'Return:' & @CRLF & #cs) ;### Debug MSGBOX

AutoIt Version: 3.3.14.2
Author:         myName
ConsoleWrite('@@ Debug(' & @ScriptLineNumber & ') : myName = ' & myName & @CRLF & '>Error code: ' & @error & @CRLF) ;### Debug Console

Script Function:
                Template AutoIt script.

#ce ----------------------------------------------------------------------------

; Script Start - Add your code below here
#AutoIt3Wrapper_Change2CUI=y
#include <AutoITConstants.au3>
;Global $fileName = "12abgeg123333.csv" ;fileName to be provided from the caller side
;Global $filePathToBeSaved = "C:\temp\" & $fileName ;filePath to be provided from the caller side which should be current result folder



If Mod($CmdLine[0], 2) <> 0 Then Exit ConsoleWrite("Invalid number of parameters! has to be a multiple of 2!" & @CRLF)
;MsgBox(16, $sTitle, "Invalid number of parameters! has to be a multiple of 2!")
If $CmdLine[0] = 0 Then Exit ConsoleWrite("No parameters passed!" & @CRLF)
;MsgBox(16, $sTitle, "No parameters passed!")

;Passing commandLine arguments
Global $fileName = $CmdLine[2] ;fileName to be provided from the caller side
Global $filePathToBeSaved = $CmdLine[1] & $fileName ;filePath to be provided from the caller side which should be current result folder


For $i = 0 To $CmdLine[0] Step 1
;
	ConsoleWrite($CmdLine[$i] & @CRLF)
Next
ConsoleWrite("Finishedwithcommandlinereading!" & @CRLF)


Global $retval =  "FileNotDownloaded"
Global $viewDownload_winHandle = WinWait("[Class:#32770]", "", 10);window popup
Global $viewDownload_ctrlHandle = ControlGetHandle("View Downloads - Internet Explorer", "", "DirectUIHWND1");window popup
Global $popupDisplayed =  False
Global $viewDownload_ctrlcmd = ControlCommand($viewDownload_winHandle,"",$viewDownload_ctrlHandle,"IsVisible")

ConsoleWrite($viewDownload_winHandle & @CRLF)
ConsoleWrite($viewDownload_ctrlHandle & @CRLF)

ConsoleWrite("Checking for View Download popup " & @CRLF)

If WinActivate($viewDownload_winHandle) And ControlCommand($viewDownload_winHandle,"",$viewDownload_ctrlHandle,"IsVisible") Then

   ConsoleWrite("Windowenter" & @CRLF)

   Send("{RIGHT}")
   sleep(500)
   Send("{RIGHT}")
   sleep(500)
   Send("{DOWN}")
   sleep(500)
   Send("{DOWN}")
   sleep(500)
   Send("{ENTER}")


   ConsoleWrite("Windowexit" & @CRLF)
   $popupDisplayed = True


Else


   ConsoleWrite("Checking for lower popup " & @CRLF)

   Global $lowerPopup_winHandle = WinGetHandle("[Class:IEFrame]");lower popup
   Global $lowerPopup_ctrlHandle = ControlGetHandle($lowerPopup_winHandle, "", "[ClassNN:DirectUIHWND1]");lower popup
   Global $lowerPopup_screenPosition= ControlGetPos($lowerPopup_winHandle,"",$lowerPopup_ctrlHandle)
   Global $aWinPos=WinGetPos($lowerPopup_winHandle)


   ConsoleWrite($lowerPopup_winHandle & @CRLF)
   ConsoleWrite($lowerPopup_ctrlHandle & @CRLF)
	sleep(500)

   If WinActivate($lowerPopup_winHandle,"") AND ControlCommand($lowerPopup_winHandle,"",$lowerPopup_ctrlHandle,"IsVisible") And $lowerPopup_screenPosition[1]> .75 * $aWinPos[3] Then
                 ; ConsoleWrite("Lowerpopenter" & @CRLF)
				  ;MouseClick($MOUSE_CLICK_LEFT,188,32,2)
                  Send("{F6}")
                  sleep(500)
                  Send("{TAB}")
                  sleep(500)
                  Send("{DOWN}")
                  sleep(500)
                  Send("a")


                  ConsoleWrite("Lowerpopexit" & @CRLF)

                  $popupDisplayed = True

   Else
                  $popupDisplayed = False
   EndIf

EndIf

If $popupDisplayed =  True Then

   ; Save as dialog - wait for Save As window
                WinWait("Save As","",10)
                ; activate Save As window
                WinActivate("Save As")
                ; path to save the file is passed as command line arugment
                ControlFocus("Save As","","[CLASS:Edit;INSTANCE:1]")
                Global $sText=""
				;ControlGetText("Save As","","Edit1")
				;If($filePathToBeSaved<>$sText)  Then
				Local $countera = 0

					  while $sText <> $filePathToBeSaved  And $countera <2
						  ConsoleWrite("loopIteration" & $countera & @CRLF)
						  ConsoleWrite("Conditionchk" & $sText <> $filePathToBeSaved & @CRLF)
						  ControlSetText("Save As","","Edit1","");clear the text
						  sleep(1000)
						  Send($filePathToBeSaved)
						  ControlClick("Save As","","[TEXT:&Save]")
						  $ret=VerifyEnteredText()
						  ConsoleWrite($ret & @CRLF)
						  If($ret<>"NOTMATCH") Then ExitLoop

						  $countera = $countera + 1
						  ;$ret=VerifyEnteredText()
						  $sText=ControlGetText("Save As","","Edit1")
							ConsoleWrite("StillinLoop" & @CRLF)

					  wend
					  sleep(2000)
						;EndIf





				;Else
				;click on save button
				ControlClick("Save As","","[TEXT:&Save]")
				sleep(2000)
				;close view downloads window
				If WinActivate($viewDownload_winHandle) And ControlCommand($viewDownload_winHandle,"",$viewDownload_ctrlHandle,"IsVisible") Then
					WinClose($viewDownload_winHandle)
					ConsoleWrite("Outofwhileloop" & @CRLF)
				EndIf

				;wait till the download completes
			    ;;Local $sAttribute = FileGetAttrib($filePathToBeSaved);
			    ;Local $counter = 0
				;while $sAttribute = "" And $counter <100
				;	sleep(1000)
				;	$sAttribute = FileGetAttrib($filePathToBeSaved)
				;	$counter = $counter + 1
				;	ConsoleWrite("Counter = " & $counter & @CRLF)
				;	ConsoleWrite("File Attribute = " & $sAttribute & @CRLF)
				;wend
				;sleep(2000)

				;ConsoleWrite("Counter After While Loop= " & $counter & @CRLF)

				;If $sAttribute  = "" Then
				;	$retval = "FileNotSaved"

			    ;Else
				;	$retval = "FileSaved"
			    ;EndIf

				$retval = "FileDownloaded"

EndIf

   ;

	ConsoleWrite($retval & @CRLF)



;verify enteredtext with sendtext
Func VerifyEnteredText()
	Global $saveAs_winHandle = WinWait("[Class:#32770; Title:Save As]", "", 10);SaveAs window
	Global $saveAs_ctrlHandle = ControlGetHandle("[Class:#32770; Title:Save As]", "", "[TEXT:OK]");

	If WinActivate($saveAs_winHandle) And ControlCommand($saveAs_winHandle,"",$saveAs_ctrlHandle,"IsVisible") Then

		ControlClick("Save As","","[TEXT:OK]")
		Global $sText=ControlGetText("Save As","","Edit1")
		Local $returnval = "NOTMATCH"
	Else
		Local $returnval = "MATCH"
	EndIf

	Return $returnval
EndFunc
