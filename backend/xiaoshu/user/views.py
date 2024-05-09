from django.shortcuts import render

# Create your views here.
from django.http import HttpResponse
from django.http import HttpRequest
from .models import User
import json

def login(request):
    if request.method == 'POST':
        data = request.body
        data = json.loads(data)
        print(f'data: {data}')
        username = data.get('username')
        password = data.get('password')

        try:
            user = User.objects.get(username=username)
        except:
            return HttpResponse('用户不存在',status=400)
        if user.password == password:
            data_json = {
                'username': user.username,
                'avatar': str(user.avatar),
                'id': user.id
            }
            return HttpResponse(json.dumps(data_json),status=200)
        else:
            return HttpResponse('密码错误',status=400)

    
def register(request):
    if request.method == 'POST':
        data = request.body
        data = json.loads(data)
        print(f'data: {data}')
        username = data.get('username')
        password = data.get('password')

        try:
            user = User.objects.get(username=username)
        except:
            user = User()
            user.username = username
            user.password = password
            user.save()
            # 返回username, avatar, id
            json_data = {
                'username': user.username,
                'avatar': str(user.avatar),
                'id': user.id
            }
            return HttpResponse(json.dumps(json_data),status=200)
        return HttpResponse('用户已存在',status=400)
