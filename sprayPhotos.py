import urllib.request
import requests
from bs4 import BeautifulSoup
import ssl
import re
ssl._create_default_https_context = ssl._create_unverified_context

url = "http://www.netbian.com/"
response = urllib.request.urlopen(url)

soup=BeautifulSoup(response,'html.parser')
print(soup)

num=0
r2=re.compile('/index_',re.I)
for link in soup.find_all('img'):
         
    
    
    x=link.get('src')
    print(x)
    imageStream = requests.get(x)
    yuan = imageStream.content
    num=num+1
    with open('/Users/mac/Desktop/Mosaic/src/smallPhotos/' + str(num) + '.jpg', 'wb') as f:
            
            print("正在写入第%d张" % num)
            f.write(yuan)  # 写进去
            f.close()  # 关闭文件


for index in range(2,100):
             re=urllib.request.urlopen(url+"/index_"+str(index)+".htm")
             s=BeautifulSoup(re,'html.parser')
             for link in s.find_all('img'):
         
    
    
                x=link.get('src')
                print(x)
                imageStream = requests.get(x)
                yuan = imageStream.content
                num=num+1
                with open('/Users/mac/Desktop/Mosaic/src/smallPhotos/' + str(num) + '.jpg', 'wb') as f:
            
                        print("正在写入第%d张" % num)
                        f.write(yuan)  # 写进去
                        f.close()  # 关闭文件
         
    
    
    
    
    

    


