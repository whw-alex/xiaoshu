# Generated by Django 4.2.5 on 2024-05-28 01:31

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Folder',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('title', models.CharField(max_length=50)),
                ('name', models.CharField(max_length=50)),
                ('modified_time', models.DateTimeField(auto_now=True)),
                ('path', models.CharField(max_length=100)),
                ('parent', models.ForeignKey(null=True, on_delete=django.db.models.deletion.CASCADE, to='user.folder')),
            ],
        ),
        migrations.CreateModel(
            name='Note',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('title', models.CharField(max_length=50)),
                ('word_count', models.IntegerField(default=0)),
                ('name', models.CharField(max_length=50)),
                ('modified_time', models.DateTimeField(auto_now=True)),
                ('path', models.CharField(max_length=100)),
                ('current_index', models.IntegerField(default=0)),
                ('parent', models.ForeignKey(null=True, on_delete=django.db.models.deletion.CASCADE, to='user.folder')),
            ],
        ),
        migrations.CreateModel(
            name='User',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('username', models.CharField(default='user', max_length=20)),
                ('password', models.CharField(default='password', max_length=20)),
                ('avatar', models.ImageField(default='@drawable/avatar_11', upload_to='avatar/')),
                ('signature', models.CharField(default='这个人很懒，什么都没有留下', max_length=100)),
                ('token', models.CharField(default='', max_length=100)),
            ],
        ),
        migrations.CreateModel(
            name='TextSegment',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('text', models.TextField()),
                ('seg_type', models.CharField(max_length=10)),
                ('index', models.IntegerField(default=0)),
                ('note', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='user.note')),
            ],
        ),
        migrations.AddField(
            model_name='note',
            name='user',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='user.user'),
        ),
        migrations.CreateModel(
            name='ImageSegment',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('image', models.CharField(max_length=100)),
                ('seg_type', models.CharField(max_length=10)),
                ('index', models.IntegerField(default=0)),
                ('note', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='user.note')),
            ],
        ),
        migrations.AddField(
            model_name='folder',
            name='user',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='user.user'),
        ),
        migrations.CreateModel(
            name='AudioSegment',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('audio', models.FileField(upload_to='audio/')),
                ('seg_type', models.CharField(max_length=10)),
                ('index', models.IntegerField(default=0)),
                ('note', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='user.note')),
            ],
        ),
    ]