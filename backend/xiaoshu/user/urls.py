from django.urls import path
from . import views

urlpatterns = [
    path('login/', views.login),
    path('register/', views.register),
    path('logout/', views.logout),
    path('profile/', views.profile),
    path('reset_password/', views.reset_password),
    path('file_list/', views.file_list),
    path('create_file/', views.create_file),
    path('note_list/', views.note_list),
    path('note_info/', views.note_info),
]