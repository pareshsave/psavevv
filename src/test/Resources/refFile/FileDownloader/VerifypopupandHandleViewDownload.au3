#cs ----------------------------------------------------------------------------
MsgBox(262144, 'Debug line ~' & @ScriptLineNumber, 'Selection:' & @CRLF & '#cs' & @CRLF & @CRLF & 'Return:' & @CRLF & #cs) ;### Debug MSGBOX

AutoIt Version: 3.3.14.2
Author:         myName
ConsoleWrite('@@ Debug(' & @ScriptLineNumber & ') : myName = ' & myName & @CRLF & '>Error code: ' & @error & @CRLF) ;### Debug Console

Script Function:
                Template AutoIt script.

#ce ----------------------------------------------------------------------------

; Script Start - Add your code below here



Global $retval =  "FileNotDownloaded"
Global $viewDownload_winHandle = WinWait("[Class:#32770]", "", 10);window popup
Global $viewDownload_ctrlHandle = ControlGetHandle("View Downloads - Internet Explorer", "", "DirectUIHWND1");window popup
Global $popupDisplayed =  False
Global $lowerpopupDisplayed =  False
Global $viewDownload_ctrlcmd = ControlCommand($viewDownload_winHandle,"",$viewDownload_ctrlHandle,"IsVisible")

ConsoleWrite($viewDownload_winHandle & @CRLF)
ConsoleWrite($viewDownload_ctrlHandle & @CRLF)

ConsoleWrite("Checking for View Download popup " & @CRLF)
verifypopup()

If($popupDisplayed = True and $lowerpopupDisplayed =  False)
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




		EndIf
EndIf

Func verifypopup()
	If WinActivate($viewDownload_winHandle) And ControlCommand($viewDownload_winHandle,"",$viewDownload_ctrlHandle,"IsVisible") Then

	   ConsoleWrite("Windowenter" & @CRLF)
	   $popupDisplayed = True
	   $lowerpopupDisplayed =  False
	   ConsoleWrite("Windowexit" & @CRLF)

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
					  WinActivate($lowerPopup_winHandle,"")
					  ConsoleWrite("Lowerpopenter" & @CRLF)

					  $popupDisplayed = True
					  $lowerpopupDisplayed =  True
					  ConsoleWrite("LowerpopupDisplayed" & @CRLF)

	   Else
					  $popupDisplayed = False
					  $lowerpopupDisplayed =  False

	   EndIf

					;Return $lowerpopupDisplayed
					;ConsoleWrite($lowerpopupDisplayed)

	EndIf

					;Return $lowerpopupDisplayed
					ConsoleWrite($lowerpopupDisplayed & @CRLF)
EndFunc





