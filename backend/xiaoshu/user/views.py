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

def file_list(request):
    if request.method == 'POST':
        data = request.body
        data = json.loads(data)
        print(f'data: {data}')
        user_id = data.get('id')
        path = data.get('path')
        cur_user = User.objects.get(id=user_id)
        if path == 'root':
            folder_list = Folder.objects.filter(user=cur_user, parent__isnull=True)
            note_list = Note.objects.filter(user=cur_user, parent__isnull=True)
        else:
            folder = Folder.objects.get(user=cur_user, path=path)
            folder_list = Folder.objects.filter(user=cur_user, parent=folder)
            note_list = Note.objects.filter(user=cur_user, parent=folder)

        data_json = {
            "labels": [],
            "titles": [],
            "contents": [],
            "dates": [],
        }

        for folder in folder_list:
            data_json['labels'].append(folder.label)
            data_json['titles'].append(folder.name)
            data_json['contents'].append("")
            data_json['dates'].append(folder.modified_time.strftime("%m月%d号"))
            print(folder.path)

        for note in note_list:
            data_json['labels'].append(note.label)
            data_json['titles'].append(note.name)
            try:
                first_text = TextSegment.objects.filter(note=note).first()
                data_json['contents'].append(first_text.text[:4]+'...')
            except:
                data_json['contents'].append('')
            data_json['dates'].append(note.modified_time.strftime("%m月%d号"))

        # data_json = {
        #     "labels": ["folder", "note", "note"],
        #     "titles": ["test1", "test2", "test3"],
        #     "contents": ["test...", "try...", "space"],
        #     "dates": ["5月10日", "5月15日", "5月20日"],
        # }
        print(data_json)
        return HttpResponse(json.dumps(data_json),status=200)


def create_file(request):
    if request.method == 'POST':
        print("try to create a new file")
        data = request.body
        data = json.loads(data)
        print(f'data: {data}')
        user_id = data.get('id')
        parent_path = data.get('location')
        name = data.get('filename')
        label = data.get('label') 

        user = User.objects.get(id=user_id)
        parent = None if parent_path == 'root' else Folder.objects.get(user=user, path=parent_path)
        path = parent_path + '/' + name
        if label == 'folder':
            if Folder.objects.filter(user=user, name=name, parent=parent).exists():
                print("重复！")
                return HttpResponse(json.dumps({'msg': '文件名重复！'}), status=400)
            Folder.objects.create(title="", user=user, name=name, parent=parent, path=path)
        else:
            if Note.objects.filter(user=user, name=name, parent=parent).exists():
                print("重复！")
                return HttpResponse(json.dumps({'msg': '文件名重复！'}), status=400)
            Note.objects.create(title="", user=user, name=name, parent=parent, path=path)

        return HttpResponse(json.dumps({'msg': '创建成功！'}), status=200)
            
        
            
