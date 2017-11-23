#version 330 core

in vec3 gfColor;

out vec4 fsColor;

void main()
{
    fsColor = vec4(gfColor,1);
}