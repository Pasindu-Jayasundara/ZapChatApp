import { Stack } from 'expo-router';
import { WebSocketProvider } from './WebSocketProvider';

const RootLayout = () => {
    return (
        <WebSocketProvider>
            <Stack screenOptions={{ animation: 'fade_from_bottom', headerShown: false }}>
                <Stack.Screen name="home"/>
                <Stack.Screen name="singleChat" />
                <Stack.Screen name="singleGroup" />
                <Stack.Screen name="profileSetUp" />
                <Stack.Screen name="newStatus" />
            </Stack>
        </WebSocketProvider>
    );
};

export default RootLayout;
