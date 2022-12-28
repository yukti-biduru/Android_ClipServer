// MediaServiceInterface.aidl
package com.example.clipserver;

// Declare any non-default types here with import statements

interface MediaServiceInterface {
    void setAudioClipNumber(int clipNumber);
    void startMedia();
    void pauseMedia();
    void resumeMedia();
    void stopMedia();
}