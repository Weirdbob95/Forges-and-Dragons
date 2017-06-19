#version 330 core

in vec3 blockPos;
flat in float surfaceID;

out vec4 color;

uniform vec3 normal;
uniform samplerBuffer positions;
uniform samplerBuffer sizes;
uniform samplerBuffer colorIndices;
uniform samplerBuffer colors;
uniform samplerBuffer shadeIndices;
uniform samplerBuffer shades;

const int SIDE_LENGTH = 128;

void main() {
    int id = int(surfaceID);
    vec3 position = texelFetch(positions, id).xyz;
    vec2 size = texelFetch(sizes, id).xy;
    int colorIndex = int(texelFetch(colorIndices, id).x);
    int shadeIndex = int(texelFetch(shadeIndices, id).x);

    vec3 adjustedPos = blockPos - position;
    vec2 texPos;
    if (normal.x != 0) texPos = adjustedPos.yz;
    else if (normal.y != 0) texPos = adjustedPos.xz;
    else texPos = adjustedPos.xy;

    int i = colorIndex + int(texPos.x) + int(size.x) * int(texPos.y);
    vec4 rawColor = texelFetch(colors, i);

    float s1 = texelFetch(shades, shadeIndex + int(texPos.x) + int(size.x + 1) * int(texPos.y)).r;
    float s2 = texelFetch(shades, shadeIndex + int(texPos.x + 1) + int(size.x + 1) * int(texPos.y)).r;
    float s3 = texelFetch(shades, shadeIndex + int(texPos.x) + int(size.x + 1) * int(texPos.y + 1)).r;
    float s4 = texelFetch(shades, shadeIndex + int(texPos.x + 1) + int(size.x + 1) * int(texPos.y + 1)).r;
    vec2 t = texPos - floor(texPos);
    float s = mix(mix(s1, s2, t.x), mix(s3, s4, t.x), t.y);

    float shade = 1 + (dot(normal, vec3(.1, .3, 1)) - 1) / 5;
    float fog = 1.0 - clamp(exp(-gl_FragCoord.z / gl_FragCoord.w * .001), 0.0, 1.0);
    color = mix(rawColor * shade, vec4(.5, .5, .5, 1.0), fog) * s;
}
