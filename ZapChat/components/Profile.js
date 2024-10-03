import { Image } from "expo-image"
import { StyleSheet, View } from "react-native"
import Ionicons from '@expo/vector-icons/Ionicons';
import { Button } from "./Button";
import { InputField } from "./InputField";

const defaultProfileImage = require("../assets/images/profileDefault.png")

export function Profile() {

    const design = {
        backgroundColor: "#d1d1d1",
        paddingHorizontal: 10,
    }
    return (
        <>
            <View style={styles.container}>
                <Image
                    style={styles.image}
                    source={defaultProfileImage}
                    contentFit="cover"
                    transition={1000}
                    contentPosition={"center"}
                />
                <Button text={"Select Profile Picture"} style={design} />
            </View>
        </>
    )
}

const styles = StyleSheet.create({

    belowView:{
        backgroundColor:"red",
        width:"100%"
    },
    container: {
        alignItems: "center",
        borderRadius: 100,
        justifyContent: "center",
        alignItems: "center",
        width: 170,
        height: 170,
    },
    image: {
        width: "100%",
        height: "100%",
        borderRadius: 100,
    }
})