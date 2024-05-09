from django.db import models

# Create your models here.
class User(models.Model):
    username = models.CharField(max_length=20, default='user')
    password = models.CharField(max_length=20, default='password')
    avatar = models.ImageField(upload_to='avatar/', default='avatar/default.jpg')