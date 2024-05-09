# Generated by Django 4.2.5 on 2024-05-09 08:57

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='User',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('username', models.CharField(default='user', max_length=20)),
                ('password', models.CharField(default='password', max_length=20)),
                ('avatar', models.ImageField(default='avatar/default.jpg', upload_to='avatar/')),
            ],
        ),
    ]