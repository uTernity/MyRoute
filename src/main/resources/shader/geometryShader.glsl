#version 150 core

layout(lines_adjacency) in;
layout(triangle_strip, max_vertices = 4) out;

in float vgType[];

uniform mat4 view;
uniform mat4 projection;

out vec3 gfColor;

float toThickness(float);
vec3 toColor(float);

void main()
{
    vec2 line = normalize((gl_in[2].gl_Position-gl_in[1].gl_Position)).xy;
    vec2 normal = vec2(-line.y, line.x);

    gl_Position = projection*view*(gl_in[1].gl_Position+vec4(normal,0,0)*toThickness(vgType[1]));
    gfColor = toColor(vgType[1]);
    EmitVertex();
    gl_Position = projection*view*(gl_in[1].gl_Position-vec4(normal,0,0)*toThickness(vgType[1]));
    gfColor = toColor(vgType[1]);
    EmitVertex();
    gl_Position = projection*view*(gl_in[2].gl_Position+vec4(normal,0,0)*toThickness(vgType[2]));;
    gfColor = toColor(vgType[2]);
    EmitVertex();
    gl_Position = projection*view*(gl_in[2].gl_Position-vec4(normal,0,0)*toThickness(vgType[2]));
    gfColor = toColor(vgType[2]);
    EmitVertex();
    EndPrimitive();
}

float toThickness(float type)
{
    int t = int(round(type));
    switch(t)
    {
        case 0: return 0.00002f;
        case 1: return 0.00004f;
        case 2: return 0.00008f;
    }
    return 0;
}

vec3 toColor(float type)
{
    int t = int(round(type));
    switch(t)
    {
        case 0: return vec3(0.3f,0.3f,0.3f);
        case 1: return vec3(1,1,1);
        case 2: return vec3(1,0,0);
    }
    return vec3(0,0,0);
}