import React from 'react';
import './style.css';

const ParticipantList = ({ participants }) => {
    return (
        <div className="participant-list">
            <h3 className="participant-list-title">
                Participants ({participants.length})
            </h3>
            <div className="participant-list-content">
                {participants.map(participant => (
                    <div 
                        key={participant.userId} 
                        className="participant-item"
                    >
                        <div className={`status-indicator ${participant.isConnected ? 'connected' : ''}`} />
                        <span className="participant-name">
                            {participant.username}
                            {participant.isLocal && ' (You)'}
                        </span>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default ParticipantList;