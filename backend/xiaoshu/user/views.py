from django.shortcuts import render

# Create your views here.
from django.http import HttpResponse
from django.http import HttpRequest
from .models import User
import json
import jwt



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
            user.token = jwt.encode({'id': user.id}, 'secret', algorithm='HS256')
            data_json = {
                'username': user.username,
                'avatar': str(user.avatar),
                'id': user.id,
                'signature': user.signature,
                'token': user.token

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
                'signature': user.signature,
                'id': user.id
            }
            return HttpResponse(json.dumps(json_data),status=200)
        return HttpResponse('用户已存在',status=400)
    
    
def logout(request):
    if request.method == 'POST':
        data = request.body
        data = json.loads(data)
        print(f'data: {data}')
        id = data.get('id')
        user = User.objects.get(id=id)
        user.token = ''
        user.save()
        return HttpResponse('退出成功',status=200)
    return HttpResponse('请求方式错误',status=400)


def profile(request):
    if request.method == 'POST':
        data = request.body
        data = json.loads(data)
        print(f'data: {data}')
        id = data.get('id')
        username = data.get('username')
        signature = data.get('signature')
        avatar = data.get('avatar')
        user = User.objects.get(id=id)
        user.username = username
        user.signature = signature
        user.avatar = avatar
        user.save()
        data_json = {
            'username': user.username,
            'avatar': str(user.avatar),
            'signature': user.signature,
            'id': user.id
        }
        return HttpResponse(json.dumps(data_json),status=200)
    if request.method == 'GET':
        id = request.GET.get('id')
        user = User.objects.get(id=id)
        data_json = {
            'username': user.username,
            'avatar': str(user.avatar),
            'signature': user.signature,
            'id': user.id
        }
        return HttpResponse(json.dumps(data_json),status=200)
    return HttpResponse('请求方式错误',status=400)

def reset_password(request):
    if request.method == 'POST':
        data = request.body
        data = json.loads(data)
        print(f'data: {data}')
        id = data.get('id')
        old_password = data.get('old_password')
        new_password = data.get('new_password')
        user = User.objects.get(id=id)
        if user.password == old_password:
            user.password = new_password
            user.save()
            data_json = {
                'username': user.username,
                'avatar': str(user.avatar),
                'signature': user.signature,
                'id': user.id
            }
            return HttpResponse(json.dumps(data_json),status=200)
        return HttpResponse('原密码错误',status=400)
    return HttpResponse('请求方式错误',status=400)