import { registerRootComponent } from "expo";
import { ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Header } from "../components/Header";
import { ChatCard } from "../components/ChatCard";
import { FloatingAction } from "react-native-floating-action";

export default function home() {

    const actions = [
        {
            text: "Create New Group",
            icon: require("../assets/images/people.png"),
            name: "1",
            position: 1,
        },
        {
            text: "New Chat",
            icon: require("../assets/images/chat.png"),
            name: "2",
            position: 2,
        }
    ]

    return (
        <SafeAreaView style={styles.safearea}>
            <Header />
            <ScrollView contentContainerStyle={styles.body}>

                <ChatCard />
                <ChatCard />

            </ScrollView>

            <FloatingAction
                color="#fc384b"
                actions={actions}
                onPressItem={name => {
                    console.log(`selected button: ${name}`);
                }}
            />
        </SafeAreaView>
    )
}

// registerRootComponent(Home)

const styles = StyleSheet.create({
    body: {
        flexGrow: 1,
        paddingTop: 15,
        // backgroundColor:"green"
    },
    safearea: {
        flex: 1
    }
})