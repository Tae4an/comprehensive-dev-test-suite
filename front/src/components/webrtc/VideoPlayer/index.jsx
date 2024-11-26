import React, { useEffect, useRef } from 'react';
import './style.css';

const VideoPlayer = ({ stream, isMuted = false, username }) => {
    const videoRef = useRef(null);

    useEffect(() => {
        if (videoRef.current && stream) {
            videoRef.current.srcObject = stream;
        }
    }, [stream]);

    return (
        <div className="video-player">
            <video
                ref={videoRef}
                autoPlay
                playsInline
                muted={isMuted}
                className="video-element"
            />
            <div className="username-label">
                {username}
                {isMuted && ' (You)'}
            </div>
        </div>
    );
};

export default VideoPlayer;