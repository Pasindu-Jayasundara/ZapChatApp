import { registerRootComponent } from "expo";
import { Alert, ScrollView, StyleSheet } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { ChatHeader } from "../components/ChatHeader";
import { StatusBar } from "expo-status-bar";
import { ChatBuble } from "../components/ChatBuble";
import { Date } from "../components/Date";
import { ChatFooter } from "../components/ChatFooter";
import { useEffect, useState } from "react";
import { useLocalSearchParams } from "expo-router";

export default function singleChat() {

    const data = useLocalSearchParams();

    return (
        <SafeAreaView style={styles.safearea}>

            <ChatHeader data={data}/>

            <ScrollView contentContainerStyle={styles.body}>

                <Date />

                <ChatBuble params={{ side: "left", time: "19:20", message: "message" }} />
                <ChatBuble params={{ side: "right", time: "19:20", message: "message" }} />

                <Date />

                <ChatBuble params={{ side: "left", time: "19:20", message: "message" }} />
                <ChatBuble params={{ side: "right", time: "19:20", message: "message" }} />
                <ChatBuble params={{ side: "left", time: "19:20", message: "message" }} />
                <ChatBuble params={{ side: "right", time: "19:20", message: "message" }} />

            </ScrollView>

            <ChatFooter />
        </SafeAreaView>
    )
}

// registerRootComponent(SingleChat)

const styles = StyleSheet.create({
    body: {
        flexGrow: 1
    },
    safearea: {
        flex: 1
    }
})