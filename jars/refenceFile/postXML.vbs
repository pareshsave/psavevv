strXMLFilePath 	=	WScript.Arguments(0)
URI				=	WScript.Arguments(1)
UsernName		=	WScript.Arguments(2)
UserPassword	=	WScript.Arguments(3)
MIPSPlatform	=	WScript.Arguments(4)

If strXMLFilePath = "" OR URI = "" OR UsernName ="" OR UserPassword = "" Then
	WScript.echo "ResponseNotFound"

else

	Const SXH_SERVER_CERT_IGNORE_ALL_SERVER_ERRORS = 13056
    Set xmlhttp = CreateObject("MSXML2.ServerXMLHTTP")
 
    xmlhttp.setOption 2, SXH_SERVER_CERT_IGNORE_ALL_SERVER_ERRORS
	xmlhttp.Open "POST", URI, False,UsernName,UserPassword
    
    
    If MIPSPlatform = "PRISM" Then
       xmlhttp.setRequestHeader "Content-Type", "application/x-www-form-urlencoded"    
       xmlhttp.setRequestHeader "accept", "application/xml"
    ELSE
    	xmlhttp.setRequestHeader "Content-Type", "text/xml"
    End If
    
    
    Dim objXMLDOM 
    'Set objXMLDOM              = Get_XML_DOM_obj(Req_XML_File_Path)
    
    Set objXMLDOM = CreateObject("Microsoft.XMLDOM") 
	objXMLDOM.async = False 
	objXMLDOM.load(strXMLFilePath) 
    
    SOAPMessage = objXMLDOM.xml
    
    If MIPSPlatform = "PRISM" Then
    	xmlhttp.send "=" & (SOAPMessage)
    ELSE
     	xmlhttp.send (SOAPMessage)
    End IF
   
    
    'WScript.echo xmlhttp.Status
    WScript.echo xmlhttp.responseText
    
  
    
    'If xmlhttp.Status = 200 Then
    '                Dim objResponseXML
    '                Set objResponseXML = xmlhttp.responseXML
    '                objResponseXML.Save(Response_XML_File_Path)
    '                Set Post_XML_Get_Save_Response = objResponseXML
   ' Else
                    'App_Specific_logging
    '                Set Post_XML_Get_Save_Response = Nothing
   ' End If
    
    Set objResponseXML     = Nothing
    Set objXMLDOM          = Nothing


End If