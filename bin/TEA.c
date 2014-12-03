#include <jni.h>
#include <stdio.h>
#include "TEA.h"

JNIEXPORT jlongArray JNICALL Java_TEA_encrypt
  (JNIEnv *env, jobject thisObj, jlongArray val, jlongArray key){
/* TEA encryption algorithm */
jlong *v = (*env)->GetLongArrayElements(env, val, NULL);
jlong *k = (*env)->GetLongArrayElements(env, key, NULL);

jlong y = v[0], z=v[1], sum = 0;
jlong delta = 0x9e3779b9, n=32;
	while (n-- > 0){
		sum += delta;
		y += (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
		z += (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
	}

	v[0] = y;
	v[1] = z;

	jlongArray out = (*env)->NewLongArray(env, 2);
	(*env)->SetLongArrayRegion(env, out, 0 , 2, v);  // copy
	return out;
}

JNIEXPORT jlongArray JNICALL Java_TEA_decrypt
  (JNIEnv * env, jobject jObj, jlongArray val, jlongArray key){
/* TEA decryption routine */
jlong *v = (*env)->GetLongArrayElements(env, val, NULL);
jlong *k = (*env)->GetLongArrayElements(env, key, NULL);

jlong n=32, sum, y=v[0], z=v[1];
jlong delta=0x9e3779b9l;

	sum = delta<<5;
	while (n-- > 0){
		z -= (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
		y -= (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
		sum -= delta;
	}
	v[0] = y;
	v[1] = z;

	jlongArray out = (*env)->NewLongArray(env, 2);
	(*env)->SetLongArrayRegion(env, out, 0 , 2, v);  // copy
	return out;
}
