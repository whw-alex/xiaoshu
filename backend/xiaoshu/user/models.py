from django.db import models

# Create your models here.
class User(models.Model):
    username = models.CharField(max_length=20, default='user')
    password = models.CharField(max_length=20, default='password')
    avatar = models.ImageField(upload_to='avatar/', default='avatar/default.jpg')
    signature = models.CharField(max_length=100, default='这个人很懒，什么都没有留下')
    token = models.CharField(max_length=100, default='')

# class File(models.Model):
#     user = models.ForeignKey(User, on_delete=models.CASCADE)
#     name = models.CharField(max_length=50)
#     modified_time = models.DateTimeField(auto_now=True)
#     parent = models.ForeignKey('self', on_delete=models.CASCADE, null=True)
#     path = models.CharField(max_length=100)

class Folder(models.Model):
    title = models.CharField(max_length=50)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    name = models.CharField(max_length=50)
    modified_time = models.DateTimeField(auto_now=True)
    parent = models.ForeignKey('self', on_delete=models.CASCADE, null=True)
    path = models.CharField(max_length=100)
    label = "folder"

class Note(models.Model):
    title = models.CharField(max_length=50)
    word_count = models.IntegerField(default=0)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    name = models.CharField(max_length=50)
    modified_time = models.DateTimeField(auto_now=True)
    parent = models.ForeignKey('self', on_delete=models.CASCADE, null=True)
    path = models.CharField(max_length=100)
    label = "note"


# segment: 基类, 子类可以是：文字，图片，语音
# class Segment(models.Model):
#     note = models.ForeignKey(Note, on_delete=models.CASCADE)
#     seg_type = models.CharField(max_length=10)
#     order = models.IntegerField()

class TextSegment(models.Model):
    text = models.TextField()
    note = models.ForeignKey(Note, on_delete=models.CASCADE)
    seg_type = models.CharField(max_length=10)
    index = models.IntegerField(default=0)

class ImageSegment(models.Model):
    image = models.ImageField(upload_to='image/')
    note = models.ForeignKey(Note, on_delete=models.CASCADE)
    seg_type = models.CharField(max_length=10)
    index = models.IntegerField(default=0)

class AudioSegment(models.Model):
    audio = models.FileField(upload_to='audio/')
    note = models.ForeignKey(Note, on_delete=models.CASCADE)
    seg_type = models.CharField(max_length=10)
    index = models.IntegerField(default=0)



