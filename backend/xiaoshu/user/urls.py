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
    path('delete_file/', views.delete_file),
    path('search/', views.search_file_list),
    path('note_list/', views.note_list),
    path('note_info/', views.note_info),
    path('upload_note_image/', views.upload_note_image),
    path('save_note_text/', views.save_note_text),
    path('chat/', views.chat),
]

# add static
from django.conf import settings
from django.conf.urls.static import static
urlpatterns += static(settings.STATIC_URL, document_root=settings.STATIC_ROOT)