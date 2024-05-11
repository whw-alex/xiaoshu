from django.db import models

# Create your models here.
class User(models.Model):
    username = models.CharField(max_length=20, default='user')
    password = models.CharField(max_length=20, default='password')
    avatar = models.ImageField(upload_to='avatar/', default='avatar/default.jpg')
    signature = models.CharField(max_length=100, default='这个人很懒，什么都没有留下')
    token = models.CharField(max_length=100, default='')

class File(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    name = models.CharField(max_length=50)
    modified_time = models.DateTimeField(auto_now=True)
    parent = models.ForeignKey('self', on_delete=models.CASCADE, null=True)

class Folder(File):
    pass

class Note(File):
    title = models.CharField(max_length=50)
    parent = models.ForeignKey(Folder, on_delete=models.CASCADE)
    word_count = models.IntegerField(default=0)


# segment: 基类, 子类可以是：文字，图片，语音
class Segment(models.Model):
    note = models.ForeignKey(Note, on_delete=models.CASCADE)
    seg_type = models.CharField(max_length=10)
    order = models.IntegerField()

class TextSegment(Segment):
    text = models.TextField()

class ImageSegment(Segment):
    image = models.ImageField(upload_to='image/')

class AudioSegment(Segment):
    audio = models.FileField(upload_to='audio/')



