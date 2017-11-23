#version 330 core

layout (location = 0) in vec3 pvPos;
layout (location = 1) in float pvType;

out float vgType;

void main()
{
    gl_Position = vec4(pvPos, 1.0);
    vgType = pvType;
}