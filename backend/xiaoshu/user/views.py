from django.shortcuts import render

# Create your views here.
from django.http import HttpResponse
from django.http import HttpRequest
from .models import User, Folder, Note, TextSegment, ImageSegment, AudioSegment
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
            # user.token = jwt.encode({'id': user.id}, 'secret', algorithm='HS256')
            data_json = {
                'username': user.username,
                'avatar': str(user.avatar),
                'id': user.id,
                'signature': user.signature,
                # 'token': user.token

            }
            print(f'data_json: {data_json}')
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
        print(f'username: {username}, password: {password}')

        try:
            user = User.objects.get(username=username)
            print('用户已存在')
            
        except:
            print('用户不存在')
            user = User()
            user.username = username
            user.password = password
            print(user.signature)
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
        user_id = data.get('id')
        user = User.objects.get(id=user_id)
        user.token = ''
        user.save()
        return HttpResponse('退出成功',status=200)
    return HttpResponse('请求方式错误',status=400)


def profile(request):
    if request.method == 'POST':
        data = request.body
        data = json.loads(data)
        print(f'data: {data}')
        user_id = data.get('id')
        username = data.get('username')
        signature = data.get('signature')
        avatar = data.get('avatar')
        user = User.objects.get(id=user_id)
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
        user_id = request.GET.get('id')
        user = User.objects.get(id=user_id)
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
        user_id = data.get('id')
        old_password = data.get('old_password')
        new_password = data.get('new_password')
        user = User.objects.get(id=user_id)
        if user.password == old_password:
            user.password = new_password
            user.save()
            data_json = {
                'id': user.id
            }
            return HttpResponse(json.dumps(data_json),status=200)
        return HttpResponse('原密码错误',status=400)
    return HttpResponse('请求方式错误',status=400)

def folder(request):
    if request.method == 'POST':
        data = request.body
        data = json.loads(data)
        print(f'data: {data}')
        user_id = data.get('id')
        path = data.get('path')
        user = User.objects.get(id=user_id)
        folder = Folder.objects.get(user=user,path=path)
        folder_list = Folder.objects.filter(user=user,parent=folder)
        note_list = Note.objects.filter(user=user,parent=folder)
        data_list = folder_list.all() + note_list.all()
        data_json = []
        for i in data_list:
            if i.label == 'folder':

                # temp_json = {
                #     'label': i.label,
                #     'title': i.title,
                #     'modified_time': i.modified_time,  
                # }
                temp_json = {}
                temp_json['label'] = i.label
                temp_json['title'] = i.title
                temp_json['modified_time'] = i.modified_time
                data_json.append(temp_json)
            else:
                temp_json = {}
                temp_json['label'] = i.label
                temp_json['title'] = i.title
                temp_json['modified_time'] = i.modified_time
                try:
                    first_text = TextSegment.objects.filter(note=i).first()
                    temp_json['content'] = first_text.text[:4]+'...'
                except:
                    temp_json['content'] = '新建笔记'
                data_json.append(temp_json)
        return HttpResponse(json.dumps(data_json),status=200)
    
def note_list(request):
    if request.method == 'POST':
        user_id = request.GET.get('id')
        path = request.GET.get('path')
        data_json = []
        try:
            user = User.objects.get(id=user_id)
            note = Note.objects.get(user=user,path=path)

            text_list = TextSegment.objects.filter(note=note)
            image_list = ImageSegment.objects.filter(note=note)
            audio_list = AudioSegment.objects.filter(note=note)
            
            data_list = text_list.all() + image_list.all() + audio_list.all()
            data_list = sorted(data_list,key=lambda x:x.index)
            for i in data_list:
                if i.seg_type == 'text':
                    temp_json = {
                        'seg_type': i.seg_type,
                        'content': i.text,
                        'index': i.index
                    }
                    data_json.append(temp_json)
                elif i.seg_type == 'image':
                    temp_json = {
                        'seg_type': i.seg_type,
                        'content': str(i.image),
                        'index': i.index
                    }
                    data_json.append(temp_json)
                elif i.seg_type == 'audio':
                    temp_json = {
                        'seg_type': i.seg_type,
                        'content': str(i.audio),
                        'index': i.index
                    }
                    data_json.append(temp_json)
            # test
            test_text = {
                'seg_type': 'text',
                'content': '这是一个测试',
                'index': 0
            }
            print(f'test_text: {test_text}')
            data_json.append(test_text)
            print(f'data_json: {data_json}')
            return HttpResponse(json.dumps(data_json),status=200)
        except:
            # return HttpResponse('笔记不存在',status=400)
            # test
            test_text = {
                'seg_type': 'text',
                'content': '这是一个测试',
                'index': 0
            }
            print(f'test_text: {test_text}')
            data_json.append(test_text)
            print(f'data_json: {json.dumps(data_json)}')
            return HttpResponse(json.dumps(data_json),status=200)
    return HttpResponse('请求方式错误',status=400)

            
def note_info(request):
    if request.method == 'POST':
        data = request.body
        data = json.loads(data)
        print(f'data: {data}')
        user_id = data.get('id')
        path = data.get('path')
        try:
            user = User.objects.get(id=user_id)
            note = Note.objects.get(user=user,path=path)
            
            data_json = {
                'title': note.title,
                'modified_time': note.modified_time
            }
            return HttpResponse(json.dumps(data_json),status=200)
        except:
            data_json = {
                'title': 'fake笔记',
                'word_count': 20,
                'modified_time': '2021-01-01 00:00:00'
            }
            return HttpResponse(json.dumps(data_json),status=200)
    return HttpResponse('请求方式错误',status=400)
            
