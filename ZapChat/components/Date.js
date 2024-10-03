import { Text, View } from "react-native"
import { StyleSheet } from "react-native"

export function Date() {

    return (
        <View style={styles.container}>
            <Text style={styles.date}>2024/12/15</Text>
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        alignSelf: "center",
        backgroundColor:"black",
        paddingHorizontal:10,
        paddingVertical:5,
        borderRadius:5,
        marginVertical:20,
    },
    date:{
        color:"white"
    },
})