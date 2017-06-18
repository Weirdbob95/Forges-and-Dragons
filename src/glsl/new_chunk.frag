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

const int SIDE_LENGTH = 32;

void main() {
    int id = int(surfaceID);
    vec3 position = texelFetch(positions, id).xyz;
    vec2 size = texelFetch(sizes, id).xy;
    int colorIndex = int(texelFetch(colorIndices, id).x);
    int shadeIndex = int(texelFetch(shadeIndices, id).x);

    vec3 adjustedPos = floor(blockPos + .5 * normal - position);
    vec2 texPos;
    if (normal.x != 0) texPos = adjustedPos.yz;
    else if (normal.y != 0) texPos = adjustedPos.xz;
    else texPos = adjustedPos.xy;

    int i = int(colorIndex + texPos.x + size.x * texPos.y);
    vec4 rawColor = texelFetch(colors, i);

    float shade = 1 + (dot(normal, vec3(.1, .3, 1)) - 1) / 5;
    float fog = 1.0 - clamp(exp(-gl_FragCoord.z / gl_FragCoord.w * .001), 0.0, 1.0);
    color = mix(rawColor * shade, vec4(.5, .5, .5, 1.0), fog);
}
