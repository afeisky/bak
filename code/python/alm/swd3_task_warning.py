#!/usr/bin/python

import sys
import os
import re
import urllib
import urllib2
import sqlite3
#import pygtk
import tempfile
#pygtk.require('2.0')
#import gtk
#import MySQLdb

from xml.dom.minidom import parse 
import xml.dom.minidom 

SHOW_SQL=True

class XML():
	def getAttribute(self,node, attrname):
		 return node.getAttribute(attrname) if node else ''

	def getNodeValue(self,node, index = 0):
		return node.childNodes[index].nodeValue if node else ''

	def getNode(self,node,name):
		return node.getElementsByTagName(name) if node else []

	def getNodeValue2(self,node1,node2):
		return self.getNodeValue(self.getNode(self.getNode(node,node1)[0],node2)[0])

	def getNodeValue3(self,node1,node2,node3):
		return self.getNodeValue(self.getNode(self.getNode(self.getNode(node,node1)[0],node2)[0],node3)[0])

class DB():
	conn=None
	def get_conn(self,path):
		conn = sqlite3.connect(path) # need: import sqlite3
		if os.path.exists(path) and os.path.isfile(path):
			pass
		else:
			conn = sqlite3.connect(':memory:')
		self.conn=conn
		return conn
	def close_all(self,conn, cursor):
		try:
			if cursor is not None:
				cursor.close()
		finally:
			if cursor is not None:
				cursor.close()

	def get_cursor(self,conn):
		if conn is not None:
			return conn.cursor()
		else:
			return get_conn('').cursor()

	def exist(self,conn,tableName):
		cur=self.get_cursor(conn)
		cur.execute("SELECT name FROM sqlite_master WHERE name='%s'"%(tableName))
		if len(cur.fetchall())>0:
			return True
		else:
			return False

	def execute(self,conn,sql):
		if sql is not None and sql != '':
			if SHOW_SQL:
				print('exectue sql:[%s]'%(sql))
			cur = self.get_cursor(conn)
			cur.execute(sql)
			conn.commit()
			self.close_all(conn, cur)
		else:
			print('the %s is empty or equal None!'%(sql))

	def record_count(self,conn,sql):
		cur=self.get_cursor(conn)
		cur.execute(sql)
		return len(cur.fetchall())

	def update(self,conn, sql, data):
		if sql is not None and sql != '':
			if data is not None:
				cu = self.get_cursor(conn)
				for d in data:
					cu.execute(sql, d)
					conn.commit()
				self.close_all(conn, cu)
		else:
			print('the %s is empty or equal None!'%(sql))
#-----------------------------
class _CONN():
	conn=DB().get_conn("swd3_alm.sqlite3"):
	def execute(self,sql):
		if sql is not None and sql != '':
			if SHOW_SQL:
				print('exectue sql:[%s]'%(sql))
			cur = self.get_cursor(self.conn)
			cur.execute(sql)
			self.conn.commit()
			self.close_all(self.conn, cur)
		else:
			print('the %s is empty or equal None!'%(sql))
	
CONN=_CONN()
#-----------------------------
SQL=\
'''
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:int="http://webservice.mks.com/10/2/Integrity" xmlns:sch="http://webservice.mks.com/10/2/Integrity/schema">
   <soapenv:Header/>
   <soapenv:Body>
      <int:getItemsByCustomQuery>
         <!--Optional:-->
         <arg0 transactionId="?">
            <sch:Username>chaofei.wu</sch:Username>
            <sch:Password>tcl000+</sch:Password>
            <!--Optional:-->
            <sch:DateFormat>?</sch:DateFormat>
            <!--Optional:-->
            <sch:DateTimeFormat>?</sch:DateTimeFormat>
            <!--Zero or more repetitions:-->
            <sch:InputField>Summary</sch:InputField>
   	       <sch:InputField>Type</sch:InputField>
   	       <sch:InputField>Assigned User</sch:InputField>
   	       <sch:InputField>Reporter Department</sch:InputField>
   	       <sch:InputField>Assignee Department</sch:InputField>
   	       <sch:InputField>Deadline Set Date</sch:InputField>
            <sch:QueryDefinition>(field[Assigned User] ="chaofei.wu","wenhui.xu","lang.feng")</sch:QueryDefinition>
         </arg0>
      </int:getItemsByCustomQuery>
   </soapenv:Body>
</soapenv:Envelope>
'''
result_SQL=\
'''<env:Envelope xmlns:env="http://schemas.xmlsoap.org/soap/envelope/">
   <env:Header/>
   <env:Body>
      <ns3:getItemsByCustomQueryResponse xmlns:ns1="http://webservice.mks.com/10/2/Integrity/schema" xmlns:ns3="http://webservice.mks.com/10/2/Integrity">
         <return>
            <ns1:Item ns1:ItemId="2149228">
               <ns1:ItemField Name="Summary">
                  <ns1:shorttext>
                     <ns1:value>[Merge patch]ALPS02688290(For_jhz6735_65t_m_alps-mp-m0.mp1-V2.39.1_P32)</ns1:value>
                  </ns1:shorttext>
               </ns1:ItemField>
               <ns1:ItemField Name="Type">
                  <ns1:type>Defect</ns1:type>
               </ns1:ItemField>
               <ns1:ItemField Name="Assigned User">
                  <ns1:UserRecord>
                     <ns1:user>
                        <ns1:value>chaofei.wu</ns1:value>
                     </ns1:user>
                     <ns1:fullname>
                        <ns1:value>Chaofei, WU(WMD PIC HZ SWD 3-HZ-TCT)</ns1:value>
                     </ns1:fullname>
                     <ns1:email>
                        <ns1:value>chaofei.wu.hz@tcl.com</ns1:value>
                     </ns1:email>
                  </ns1:UserRecord>
               </ns1:ItemField>
               <ns1:ItemField Name="Reporter Department">
                  <ns1:shorttext>
                     <ns1:value>WMD-PIC HZ-SWD 2</ns1:value>
                  </ns1:shorttext>
               </ns1:ItemField>
               <ns1:ItemField Name="Assignee Department">
                  <ns1:shorttext>
                     <ns1:value>WMD-PIC HZ-SWD 3</ns1:value>
                  </ns1:shorttext>
               </ns1:ItemField>
            </ns1:Item>
            <ns1:Item ns1:ItemId="2138410">
               <ns1:ItemField Name="Summary">
                  <ns1:shorttext>
                     <ns1:value>modify root veritify in user version to cts nfc test.</ns1:value>
                  </ns1:shorttext>
               </ns1:ItemField>
               <ns1:ItemField Name="Type">
                  <ns1:type>Task</ns1:type>
               </ns1:ItemField>
               <ns1:ItemField Name="Assigned User">
                  <ns1:UserRecord>
                     <ns1:user>
                        <ns1:value>wenhui.xu</ns1:value>
                     </ns1:user>
                     <ns1:fullname>
                        <ns1:value>Wenhui, XU(WMD PIC HZ SWD 3-HZ-TCT)</ns1:value>
                     </ns1:fullname>
                     <ns1:email>
                        <ns1:value>wenhui.xu.hz@tcl.com</ns1:value>
                     </ns1:email>
                  </ns1:UserRecord>
               </ns1:ItemField>
               <ns1:ItemField Name="Reporter Department">
                  <ns1:shorttext>
                     <ns1:value>WMD-PIC HZ-SWD 3</ns1:value>
                  </ns1:shorttext>
               </ns1:ItemField>
               <ns1:ItemField Name="Assignee Department">
                  <ns1:shorttext>
                     <ns1:value>WMD-PIC HZ-SWD 3</ns1:value>
                  </ns1:shorttext>
               </ns1:ItemField>
            </ns1:Item>
            <ns1:Item ns1:ItemId="2127594">
               <ns1:ItemField Name="Summary">
                  <ns1:shorttext>
                     <ns1:value>[steting] Set "Scheduled power off and power on "in same time, the phone will not boot</ns1:value>
                  </ns1:shorttext>
               </ns1:ItemField>
               <ns1:ItemField Name="Type">
                  <ns1:type>Defect</ns1:type>
               </ns1:ItemField>
               <ns1:ItemField Name="Assigned User">
                  <ns1:UserRecord>
                     <ns1:user>
                        <ns1:value>lang.feng</ns1:value>
                     </ns1:user>
                     <ns1:fullname>
                        <ns1:value>Lang, FENG(WMD PIC HZ SWD 3-HZ-TCT)</ns1:value>
                     </ns1:fullname>
                     <ns1:email>
                        <ns1:value>lang.feng@tcl.com</ns1:value>
                     </ns1:email>
                  </ns1:UserRecord>
               </ns1:ItemField>
               <ns1:ItemField Name="Reporter Department">
                  <ns1:shorttext>
                     <ns1:value>HuiZhou-longcheer</ns1:value>
                  </ns1:shorttext>
               </ns1:ItemField>
               <ns1:ItemField Name="Assignee Department">
                  <ns1:shorttext>
                     <ns1:value>WMD-PIC HZ-SWD 3</ns1:value>
                  </ns1:shorttext>
               </ns1:ItemField>
            </ns1:Item>
            <ns1:Item ns1:ItemId="2105382">
               <ns1:ItemField Name="Summary">
                  <ns1:shorttext>
                     <ns1:value>[Monitor][USB][MTP]There are two SD Card on PC when connect with PC via USB cable</ns1:value>
                  </ns1:shorttext>
               </ns1:ItemField>
               <ns1:ItemField Name="Type">
                  <ns1:type>Defect</ns1:type>
               </ns1:ItemField>
               <ns1:ItemField Name="Assigned User">
                  <ns1:UserRecord>
                     <ns1:user>
                        <ns1:value>lang.feng</ns1:value>
                     </ns1:user>
                     <ns1:fullname>
                        <ns1:value>Lang, FENG(WMD PIC HZ SWD 3-HZ-TCT)</ns1:value>
                     </ns1:fullname>
                     <ns1:email>
                        <ns1:value>lang.feng@tcl.com</ns1:value>
                     </ns1:email>
                  </ns1:UserRecord>
               </ns1:ItemField>
               <ns1:ItemField Name="Reporter Department">
                  <ns1:shorttext>
                     <ns1:value>HuiZhou-longcheer</ns1:value>
                  </ns1:shorttext>
               </ns1:ItemField>
               <ns1:ItemField Name="Assignee Department">
                  <ns1:shorttext>
                     <ns1:value>WMD-PIC HZ-SWD 3</ns1:value>
                  </ns1:shorttext>
               </ns1:ItemField>
            </ns1:Item>
         </return>
      </ns3:getItemsByCustomQueryResponse>
   </env:Body>
</env:Envelope>
'''


def get_summary(response):
	p = r'<.*?:ItemField Name="Summary"><.*:shorttext><.*:value>(.*)</.*:value></.*:shorttext>'
	m = re.search(p, response)
	if m:
		return m.group(1)
	return 'Get Summary Error!!!'
	
doc = xml.dom.minidom.parse("alm.xml") 
root = doc.documentElement
node_key = 'return'
nodes = XML().getNode(root,node_key)
print("len=%d"%(len(nodes)))
item_id = ""

COUNT=0


class _ALM():

	def getNodeitem(self,node):
		global COUNT
		if (COUNT>1000):
			return
		COUNT+=1
		
		for sub in node.childNodes:
			#if sub.nodeType == sub.ELEMENT_NODE:
			print sub.nodeName
			if (sub.nodeName=="return"):
				print("find return!-------")
				return sub
				break
			if len(sub.childNodes)>0:
				return self.getNodeitem(sub)
		return None


	def parseItem(self,parentNode):
		itemID=XML().getAttribute(parentNode,'ns1:ItemId')
		print("itemID="+itemID)
		parentNodes=XML().getNode(parentNode,"ns1:ItemField")
		itemName=""
		Summary=""
		Type=""
		Assigned_User=""
		for node in parentNodes:
			if node.nodeType == parentNode.ELEMENT_NODE:
				#print("   %s->[%s]"%(node.nodeName,node.nodeName))
				itemName=XML().getAttribute(node,'Name')
				if itemName=="Summary":
					#Summary=XML().getNodeValue(XML().getNode(XML().getNode(node,"ns1:shorttext")[0],"ns1:value")[0])
					Summary=XML().getNodeValue2("ns1:shorttext","ns1:value")
					#print("Summary="+Summary)
				elif itemName=="Type":
					#Type=XML().getNodeValue(XML().getNode(XML().getNode(node,"ns1:shorttext")[0],"ns1:value")[0])
					Type=XML().getNodeValue(XML().getNode(node,"ns1:type")[0])
					#print("Type="+Type)
				elif itemName=="Assigned User":
					Assigned_User=XML().getNodeValue3("ns1:UserRecord","ns1:email","ns1:value")
					#print("Assigned User="+Assigned_User)
		return {"itemid":itemID,"summary":Summary,"type":Type,"assigned_user":Assigned_User}


print("-------------------")
ALM=_ALM()
return_node=ALM.getNodeitem(root)
print(return_node)
if not return_node==None:
	nodes=XML().getNode(root,"ns1:Item")
	conn=DB().get_conn("swd3_alm.sqlite3")
	if DB().exist(conn,"alm1"):
		DB().execute(conn,"create table alm (alm_id integer primary key,summary varchar(100),type text varchar(20),assigned_user varchar(30))")
	for node in return_node.childNodes:
		if node.nodeType == node.ELEMENT_NODE:
			print("%s->[%s]"%(return_node.nodeName,node.nodeName))
			
			line=ALM.parseItem(node)
			print(line)
			if (DB().record_count(conn,"select * from alm where alm_id='%s'"%(line['itemid']))):
				#update_sql = "UPDATE alm SET name = ? WHERE alm_id ='%s'"%(line['itemid'])
				#data = [('id', index),
						#('alm_id', itemID),
						#('type', self.Type),
						#('assigned_user', self.Assigned_User)]
				#DB().update(conn, update_sql, data)
				sql="update alm set alm_id='%s',summary='%s',type='%s',assigned_user='%s' WHERE alm_id ='"+line['itemid']+"'"
			else:
				sql="insert into alm values ('%s','%s','%s','%s')"
			DB().execute(conn,sql%(line['itemid'],line['summary'],line['type'],line['assigned_user']))
			conn.commit()
	cur=DB().get_cursor(conn)
	cur.execute("select * from alm")
	for item in cur.fetchall():
		for element in item:
			print element,
		print

			
print("-------done!---------")
