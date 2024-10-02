import { registerRootComponent } from "expo";
import { ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Header } from "../components/Header";
import { ChatCard } from "../components/ChatCard";


export function Home() {

    return (
        <SafeAreaView style={styles.safearea}>
            <Header/>
            <ScrollView contentContainerStyle={styles.body}>

                <ChatCard/>

            </ScrollView>
        </SafeAreaView>
    )
}

registerRootComponent(Home)

const styles = StyleSheet.create({
    body:{
        flex:1,
        // backgroundColor:"green"
    },
    safearea: {
        flex: 1
    }
})