from django.shortcuts import render

# Create your views here.
from django.http import HttpResponse
from django.http import HttpRequest
from .models import User, Folder, Note, TextSegment, ImageSegment, AudioSegment
import json
import jwt
from datetime import datetime
from time import sleep



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
        data = request.body
        data = json.loads(data)
        print(f'data: {data}')
        user_id = data.get('id')
        path = data.get('path')
        data_json = []
        # try:
        print(f'user_id: {user_id}, path: {path}')
        user = User.objects.get(id=user_id)
        note = Note.objects.get(user=user,path=path)

        text_list = TextSegment.objects.filter(note=note)
        image_list = ImageSegment.objects.filter(note=note)
        audio_list = AudioSegment.objects.filter(note=note)
        
        data_list = list(text_list.all()) + list(image_list.all()) + list(audio_list.all())
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
        
        # # test
        # test_text = {
        #     'seg_type': 'text',
        #     'content': '这是一个测试',
        #     'index': 0
        # }
        # print(f'test_text: {test_text}')
        # data_json.append(test_text)
        print(f'data_json: {data_json}')
        return HttpResponse(json.dumps(data_json),status=200)
        # except:
        #     # return HttpResponse('笔记不存在',status=400)
        #     # test
        #     test_text = {
        #         'seg_type': 'text',
        #         'content': '这是一个测试',
        #         'index': 0
        #     }
        #     print(f'test_text: {test_text}')
        #     data_json.append(test_text)
        #     print(f'data_json: {json.dumps(data_json)}')
        #     return HttpResponse(json.dumps(data_json),status=200)
    return HttpResponse('请求方式错误',status=400)

            
def note_info(request):
    if request.method == 'POST':
        data = request.body
        data = json.loads(data)
        print(f'data: {data}')
        user_id = data.get('id')
        path = data.get('path')
        user = User.objects.get(id=user_id)
        note = Note.objects.get(user=user,path=path)
        data_json = {
                'title': note.name,
                'modified_time': note.modified_time.strftime("%Y-%m-%d %H:%M:%S")
            }
        return HttpResponse(json.dumps(data_json),status=200)
    #     try:
    #         user = User.objects.get(id=user_id)
    #         note = Note.objects.get(user=user,path=path)
            
    #         data_json = {
    #             'title': note.title,
    #             'modified_time': note.modified_time
    #         }
    #         return HttpResponse(json.dumps(data_json),status=200)
    #     except:
    #         data_json = {
    #             'title': 'fake笔记',
    #             'word_count': 20,
    #             'modified_time': '2021-01-01 00:00:00'
    #         }
    #         return HttpResponse(json.dumps(data_json),status=200)
    # return HttpResponse('请求方式错误',status=400)

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
            TextSegment.objects.create(text='placeholder', note=Note.objects.get(user=user, path=path), seg_type='text', index=0)
            note = Note.objects.get(user=user, path=path)
            note.current_index = 1
            note.save()

        return HttpResponse(json.dumps({'msg': '创建成功！'}), status=200)
            
        
def upload_note_image(request):
    print("try to upload a new image")
    print(f'request: {request}')
    print(f'request.FILES: {request.FILES}')
    if request.method == 'POST' and request.FILES.get('file'):
        print("try to upload a new image")
        data = request.POST
        print(f'data: {data}')
        image = request.FILES.get('file')
        user_id = data.get('id')
        print(f'user_id: {user_id}')
        user = User.objects.get(id=user_id)
        path = data.get('path')
        note = Note.objects.get(user=user, path=data.get('path'))
        current_index = note.current_index
        print(f'current_index: {current_index}')
        # save image
        dir_path = f'./static/image/{user_id}/{path}'
        import os
        if not os.path.exists(dir_path):
            os.makedirs(dir_path)
        image_path = os.path.join(dir_path, str(current_index) + '_' + image.name)
        with open(image_path, 'wb') as f:
            for chunk in image.chunks():
                f.write(chunk)
        image_path = f'http://10.0.2.2:8000/static/image/{user_id}/{path}/{str(current_index) + "_" + image.name}'
        ImageSegment.objects.create(image=image_path, note=note, seg_type='image', index=current_index)
        note.current_index += 1
        note.save()
        TextSegment.objects.create(text='placeholder', note=note, seg_type='text', index=note.current_index)
        note.current_index += 1
        note.save()
    return HttpResponse(json.dumps({'msg': '创建成功！'}), status=200)

def upload_note_audio(request):
    if request.method == 'POST' and request.FILES.get('file'):
        print("try to upload a new audio")
        data = request.POST
        print(f'data: {data}')
        audio = request.FILES.get('file')
        print(f'audio: {audio}')
        user_id = data.get('id')
        print(f'user_id: {user_id}')
        user = User.objects.get(id=user_id)
        path = data.get('path')
        note = Note.objects.get(user=user, path=data.get('path'))
        current_index = note.current_index
        print(f'current_index: {current_index}')
        # save audio
        dir_path = f'./static/audio/{user_id}/{path}'
        import os
        if not os.path.exists(dir_path):
            os.makedirs(dir_path)
        audio_path = os.path.join(dir_path, str(current_index) + '_' + audio.name)
        with open(audio_path, 'wb') as f:
            for chunk in audio.chunks():
                f.write(chunk)
        audio_path = f'http://10.0.2.2:8000/static/audio/{user_id}/{path}/{str(current_index) + "_" + audio.name}'
        AudioSegment.objects.create(audio=audio_path, note=note, seg_type='audio', index=current_index)
        note.current_index += 1
        note.save()
        TextSegment.objects.create(text='placeholder', note=note, seg_type='text', index=note.current_index)
        note.current_index += 1
        note.save()
    return HttpResponse(json.dumps({'msg': '创建成功！'}), status=200)


def save_note_text(request):
    if request.method == 'POST':
        data = request.body
        data = json.loads(data)
        print(f'data: {data}')
        user_id = data.get('id')
        path = data.get('path')
        textList = data.get('textList')
        user = User.objects.get(id=user_id)
        note = Note.objects.get(user=user, path=path)
        for text_pair in textList:
            index = text_pair.get('first')
            content = text_pair.get('second')
            print(f'index: {index}, content: {content}')
            text = TextSegment.objects.get(note=note, index=index)
            text.text = content
            text.save()
        return HttpResponse(json.dumps({'msg': '创建成功！'}), status=200)
    return HttpResponse('请求方式错误',status=400)
     

def chat(request):
    if request.method == 'POST':
        data = request.body
        data = json.loads(data)
        messages = [{"role": "user", "content": data.get('prompt')}]
        print("message:", messages)

        from openai import OpenAI
        client = OpenAI(
            api_key = 'sk-CIvPbVOuIIdTR17Mw5SlxMpqkiivo3SKXZk9iZnXaUONAKET',
            base_url = "https://api.moonshot.cn/v1",
        )
        
        completion = client.chat.completions.create(
            model = "moonshot-v1-8k",
            messages = messages,
        )
        
        print(completion.choices[0].message.content)
        return HttpResponse(json.dumps({'answer': completion.choices[0].message.content}), status=200)

        sleep(5)
        return HttpResponse(json.dumps({'answer': '你要这么问，小术我百口莫辩！我不知道该说什么！'}), status=200)
    
    return HttpResponse('请求方式错误',status=400)
    